package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.DateUtil;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserRoleDO;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 角色操作service实现类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/6 9:28
 * @see RoleServiceImpl
 * @since JDK1.8
 */
@Service
@Transactional
public class RoleServiceImpl implements IRoleService {

    private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private IRoleMapper roleMapper;

    @Autowired
    private IRolePermissionMapper rolePermissionMapper;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private ISeqBp seqBp;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    IOperationBp operationBp;
    @Autowired
    IPermitStatBp permitStatBp;

    /**
     * Description: 1、根据多个条件获取角色列表 2、admin可以查看所有角色；业务人员只能查看自己创建的角色
     *
     * @param : roleVO
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:09
     * @see
     */
    @Override
    public ResultVO<PageResultVO> getRolesByInfo(String jsonStr) {
        /* 1、将json字符串转为Json对象 */
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /* 2、获取参数并校验 */
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        /*组装查询条件queryMap*/
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("createBy", operator);
        //RoleVO roleVO = StringUtility.parseObject(jsonObject.getString("role"), RoleVO.class);
        RoleVO roleVo = jsonObject.getObject("role", RoleVO.class);
        if (roleVo != null) {
            String[] roleNames = roleVo.getRoleName().split(System.getProperty("line.separator"));
            queryMap.put("roleNames", roleNames);
        }
        /*管理员角色不需要createBy条件，可以查看所有的角色*/
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (isAdmin) {
            queryMap.put("createBy", "");
        }

        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
            throw new URCBizException("pageNumber or  pageData is not a num", ErrorCode.E_000003);
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);
        List<RoleDO> roleDOS = roleMapper.listRolesByPage(queryMap);
        /* 4、List<DO> 转 List<VO> */
        List<RoleVO> roleVOS = convertDoToVO(roleDOS);
        /* 5、获取总条数 */
        Long total = roleMapper.getCounts(queryMap);
        PageResultVO pageResultVO = new PageResultVO(roleVOS, total, queryMap.get("pageSize").toString());
        return VoHelper.getSuccessResult(pageResultVO);
    }

    private List<RoleVO> convertDoToVO(List<RoleDO> roleDOS) {
        List<RoleVO> roleVOS = new ArrayList<>();
        for (RoleDO roleDO : roleDOS) {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(roleDO, roleVO);
            roleVO.setRoleId(roleDO.getRoleId().toString());
            roleVO.setCreateTimeStr(roleDO.getCreateTime() != null ? DateUtil.formatDate(roleDO.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : null);
            roleVO.setModifiedTimeStr(roleDO.getModifiedTime() != null ? DateUtil.formatDate(roleDO.getModifiedTime(), "yyyy-MM-dd HH:mm:ss") : null);
            roleVO.setExpireTimeStr(roleDO.getExpireTime() != null ? DateUtil.formatDate(roleDO.getExpireTime(), "yyyy-MM-dd HH:mm:ss") : null);
            roleVO.setEffectiveTimeStr(roleDO.getEffectiveTime() != null ? DateUtil.formatDate(roleDO.getEffectiveTime(), "yyyy-MM-dd HH:mm:ss") : null);
            roleVOS.add(roleVO);
        }
        return roleVOS;
    }

    /**
     * Description:新增或更新角色基础信息、功能权限、用户
     *
     * @param : jsonStr
     * @return: ResultVO
     * @auther: lvcr
     * @date: 2018/6/13 20:42
     * @see
     */
    @Transactional
    @Override
    public ResultVO addOrUpdateRoleInfo(String jsonStr) {
        /* 1、将json字符串转为Json对象 */
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /* 2、获取参数并校验 */
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        RoleVO roleVO = StringUtility.parseObject(jsonObject.getString("role"), RoleVO.class);
        if (roleVO == null) {
            throw new URCBizException("parameter role is null", ErrorCode.E_000002);
        }
        String roleIdStr = roleVO.getRoleId();
        Long roleId = StringUtil.isEmpty(roleIdStr)?null:Long.valueOf(roleVO.getRoleId());
          /*获取角色原关联的用户userName*/
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(roleId);
        List<String> oldRelationUsers = userRoleMapper.getUserNameByRoleId(userRoleDO);
          /* 3.判断当前用户是否是管理员——管理员管理员可以直接进行操作 */
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        RoleDO opRoleDO = roleMapper.getRoleByRoleId(String.valueOf(roleId));
        if (isAdmin) {
            insertOrUpdateRole(operator, roleVO, opRoleDO);
        } else {
            /* 4.1 非管理员———该角色存在但是创建人不是当前用户 */
            if (opRoleDO != null && !opRoleDO.getCreateBy().equals(operator)) {
                throw new URCBizException(String.format("该角色的创建人不是当前用户 当前用户:%s ,角色:%s", operator, opRoleDO.getRoleName()), ErrorCode.E_100003);
            } else {
                /* 4.2 非管理员———a.该角色存在且创建人是当前用户；b.该角色不存在 */
                insertOrUpdateRole(operator, roleVO, opRoleDO);
            }

        }

         /*更新用户功能权限缓存*/
        List<String> lstUserName = new ArrayList<>();
        /*获取角色现在关联的用户userName*/
        List<String> newRelationUsers = roleVO.getLstUserName();
        /*添加角色原来关联的用户列表*/
        if(oldRelationUsers!=null && !oldRelationUsers.isEmpty()){
            lstUserName.addAll(oldRelationUsers);
        }
        /*添加角色现在关联的用户列表*/
        if(newRelationUsers!=null && !newRelationUsers.isEmpty()) {
            lstUserName.addAll(newRelationUsers);
        }
        if (lstUserName != null && !lstUserName.isEmpty()) {
            /*去重*/
            lstUserName = removeDuplicate(lstUserName);
            permitStatBp.updateUserPermitCache(lstUserName);
        }
        return VoHelper.getSuccessResult();
    }

    private List<String> removeDuplicate(List<String> lstUserName) {
        HashSet h = new HashSet(lstUserName);
        lstUserName.clear();
        lstUserName.addAll(h);
        return lstUserName;
    }

    /**
     * Description: 操作角色表、角色-操作权限关系表、用户-角色关系表
     *
     * @param : operator roleVO
     * @return: ResultVO
     * @auther: lvcr
     * @date: 2018/6/13 20:44
     * @see
     */
    private void insertOrUpdateRole(String operator, RoleVO roleVO, RoleDO opRoleDO) {
        if (opRoleDO == null) {
            RoleDO roleDO = new RoleDO();
            BeanUtils.copyProperties(roleVO, roleDO);
            Long roleId = seqBp.getNextRoleId();
            roleDO.setRoleId(roleId);
            roleDO.setCreateBy(operator);
            roleDO.setCreateTime(new Date());
            roleDO.setModifiedBy(operator);
            roleDO.setModifiedTime(new Date());
            checkEffective(roleDO);
            int rtn = roleMapper.insert(roleDO);
             /*批量新增角色-操作权限关系数据*/
            insertBatchRolePermission(roleVO, operator, roleDO.getRoleId());
            /*批量新增用户-角色关系数据*/
            insertBatchUserRole(roleVO, operator, roleDO.getRoleId());
        } else {
            RoleDO roleDO = new RoleDO();
            BeanUtils.copyProperties(roleVO, roleDO);
            roleDO.setModifiedBy(operator);
            roleDO.setModifiedTime(new Date());
            roleDO.setRoleId(opRoleDO.getRoleId());
            checkEffective(roleDO);
            roleMapper.updateByRoleId(roleDO);
              /*删除原有角色-权限关系数据*/
            rolePermissionMapper.deleteByRoleId(roleDO.getRoleId());
            /*批量新增角色-操作权限关系数据*/
            insertBatchRolePermission(roleVO, operator, roleDO.getRoleId());
            /*删除原有用户-角色关系数据*/
            userRoleMapper.deleteByRoleId(roleDO.getRoleId());
            /*批量新增用户-角色关系数据*/
            insertBatchUserRole(roleVO, operator, roleDO.getRoleId());
        }
    }

    /**
     * Description: 不是永久有效，需要判断有效期，如果有效期无效，则把状态置为不可用
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/26 11:39
     * @see
     */
    private void checkEffective(RoleDO roleDO) {
        if (!roleDO.isForever()) {
            Date effectiveTime = roleDO.getEffectiveTime();
            Date expireTime = roleDO.getExpireTime();
            if (effectiveTime == null || expireTime == null) {
                roleDO.setActive(Boolean.FALSE);
            }
            Date nowTime = new Date();
            if (effectiveTime.after(nowTime) || expireTime.before(nowTime)) {
                roleDO.setActive(Boolean.FALSE);
            }
        }
    }

    /**
     * Description: 批量新增用户-角色关系数据
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/19 16:35
     * @see
     */
    private void insertBatchUserRole(RoleVO roleVO, String operator, Long roleId) {
        List<String> lstUserName = roleVO.getLstUserName();
        if (lstUserName != null && !lstUserName.isEmpty()) {
            List<UserRoleDO> userRoleDOS = new ArrayList<>();
            for (String userName : lstUserName) {
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserName(userName);
                userRoleDO.setRoleId(roleId);
                userRoleDO.setCreateBy(operator);
                userRoleDO.setCreateTime(new Date());
                userRoleDO.setModifiedBy(operator);
                userRoleDO.setModifiedTime(new Date());
                userRoleDOS.add(userRoleDO);
            }
            userRoleMapper.insertBatch(userRoleDOS);
        }
    }

    /**
     * Description: 批量新增角色-操作权限关系数据
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/19 16:35
     * @see
     */
    private void insertBatchRolePermission(RoleVO roleVO, String operator, Long roleId) {
        List<PermissionVO> permissionVOS = roleVO.getSelectedContext();
        if (permissionVOS != null && !permissionVOS.isEmpty()) {
            List<RolePermissionDO> rolePermissionDOS = new ArrayList<>();
            for (PermissionVO permissionVO : permissionVOS) {
                RolePermissionDO rolePermissionDO = new RolePermissionDO();
                rolePermissionDO.setCreateTime(new Date());
                rolePermissionDO.setCreateBy(operator);
                rolePermissionDO.setModifiedTime(new Date());
                rolePermissionDO.setModifiedBy(operator);
                rolePermissionDO.setSelectedContext(permissionVO.getSysContext());
                rolePermissionDO.setRoleId(roleId);
                rolePermissionDO.setSysKey(permissionVO.getSysKey());
                rolePermissionDOS.add(rolePermissionDO);
            }
            rolePermissionMapper.insertAndUpdateBatch(rolePermissionDOS);
        }
    }

    /**
     * Description: 根据角色Id获取角色信息
     *
     * @param : roleId
     * @return:
     * @auther: lvcr
     * @date: 2018/6/8 17:32
     * @see
     */
    @Override
    public ResultVO<RoleVO> getRoleByRoleId(String jsonStr) {
        /* 1、将json字符串转为Json对象 */
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
		/* 2、获取参数并校验 */
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        String roleIdStr = jsonObject.getString("roleId");
        if (!StringUtil.isNum(roleIdStr)) {
            throw new URCBizException("parameter roleId is not a num", ErrorCode.E_000003);
        }
        Long roleId = Long.valueOf(roleIdStr);
        RoleDO roleDO = roleMapper.getRoleDatasByRoleId(roleId);
        if (roleDO == null) {
            throw new URCBizException("role data is null where roleId is:" + roleIdStr, ErrorCode.E_000003);
        }
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (!isAdmin && !operator.equals(roleDO.getCreateBy())) {
            throw new URCBizException("当前用户不是超级管理员，并且角色不是当前用户创建" + roleIdStr, ErrorCode.E_000003);
        }
        /*3、将roleDO转为roleVO*/
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(roleDO, roleVO);
        /*4、获取该角色已赋权的功能权限数据*/
        List<RolePermissionDO> rolePermissionDOS = roleDO.getPermissionDOList();
        /*5、获取该角色对应的系统的可用功能权限数据*/
        Map<String, PermissionDO> systemPermissionDos = permissionMapper.getSysContextByRoleId(roleId);
        /*6、将角色已赋权的权限数据与角色对应系统最新的权限数据做对比，筛选并组装角色可用的权限数据*/
        filterRolePermissionDOS(rolePermissionDOS, systemPermissionDos);
        /*7、组装roleVO里的selectedContext*/
        List<PermissionVO> permissionVOS = new ArrayList<>();
        convertPermissionDOToVO(rolePermissionDOS, permissionVOS);
        roleVO.setSelectedContext(permissionVOS);
        /*5、组装roleVo里的lstUserName*/
        List<String> lstUserName = new ArrayList<>();
        List<UserRoleDO> userRoleDOS = roleDO.getUserRoleDOS();
        for (UserRoleDO userRoleDO : userRoleDOS) {
            lstUserName.add(userRoleDO.getUserName());
        }
        roleVO.setLstUserName(lstUserName);

        return VoHelper.getSuccessResult(roleVO);
    }

    /**
     * Description: 将角色已赋权的权限数据与角色对应系统最新的权限数据做对比，筛选并组装角色可用的权限数据
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/26 15:46
     * @see
     */
    private void filterRolePermissionDOS(List<RolePermissionDO> rolePermissionDOS, Map<String, PermissionDO> systemPermissionDos) {
        for (RolePermissionDO rolePermissionDO : rolePermissionDOS) {
            String sysKey = rolePermissionDO.getSysKey();
            PermissionDO permissionDO = systemPermissionDos.get(sysKey);
            String filterSelContext = userValidateBp.cleanDeletedNode(rolePermissionDO.getSelectedContext(), permissionDO.getSysContext());
            rolePermissionDO.setSelectedContext(filterSelContext);
        }
    }

    private void convertPermissionDOToVO(List<RolePermissionDO> rolePermissionDOS, List<PermissionVO> permissionVOS) {
        for (RolePermissionDO rolePermissionDO : rolePermissionDOS) {
            PermissionVO permissionVO = new PermissionVO();
            permissionVO.setSysKey(rolePermissionDO.getSysKey());
            permissionVO.setSysName(rolePermissionDO.getSysName());
            permissionVO.setSysContext(rolePermissionDO.getSelectedContext());
            permissionVOS.add(permissionVO);
        }

    }

    /**
     * Description: 获取角色关联的用户 1、… 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:35
     * @see
     */
    @Override
    public ResultVO getUserByRoleId(String operator, String roleId) {
        UserRoleDO userRole = new UserRoleDO();
        userRole.setRoleId(Long.parseLong(roleId));
        if (!roleMapper.isSuperAdminAccount(operator)) {
            userRole.setCreateBy(operator);
        }
        List<UserDO> userList = userMapper.getUserByRoleId(userRole);
        return VoHelper.getSuccessResult(userList);
    }

    /**
     * Description: 批量删除角色 包括角色-功能权限关系 用户-角色关系 角色
     *
     * @param : lstRoleId
     * @auther: lvcr
     * @date: 2018/6/6 14:44
     * @see
     */
    @Transactional
    @Override
    public ResultVO deleteRoles(String jsonStr) {
         /*1、将json字符串转为Json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、获取参数并校验*/
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        String lstRoleIdStr = jsonObject.getString("lstRoleId");
        if (StringUtil.isEmpty(lstRoleIdStr)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        List<Long> lstRoleId = StringUtility.jsonToList(lstRoleIdStr, Long.class);
		/* 非管理员用户只能管理自己创建的角色 */
        Map dataMap = new HashMap();
        if (roleMapper.isSuperAdminAccount(operator)) {
            dataMap.put("createBy", "");
        } else {
            dataMap.put("createBy", operator);
        }
        dataMap.put("roleIds", lstRoleId);
        /*3、获取roleIds角色对应的用户名*/
        List<String> userNames = userRoleMapper.listUserNamesByRoleIds(dataMap);
		/*4、删除角色信息  包括角色基本信息、角色-操作权限关系数据、用户-角色关系数据*/
        roleMapper.deleteBatchRoleDatas(dataMap);
        /*5、更新用户操作权限冗余表和缓存*/
        permitStatBp.updateUserPermitCache(userNames);

        return VoHelper.getSuccessResult();
    }


    /**
     * Description:
     * 1、分配权限--获取多个角色已有的功能权限
     * 2、获取之前要将角色已有的功能权限与业务系统的功能权限定义求交集；
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:52
     * @see
     */
    @Override
    public ResultVO getRolePermission(String operator, List<String> lstRoleId) {
        List<RoleVO> roleVoList = new ArrayList<RoleVO>();
        if (lstRoleId != null && lstRoleId.size() > 0) {
            for (int i = 0; i < lstRoleId.size(); i++) {
                RoleVO roleVO = new RoleVO();
                RolePermissionDO permissionDO = new RolePermissionDO();
                permissionDO.setRoleId(Long.parseLong(lstRoleId.get(i)));
                if (!roleMapper.isSuperAdminAccount(operator)) {
                    permissionDO.setCreateBy(operator);
                }
                List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRolePermission(permissionDO);
                List<PermissionVO> permissionVOs = new ArrayList<PermissionVO>();
                for (RolePermissionDO rolePermissionDO : rolePermissionList) {
                    PermissionDO permission = permissionMapper.getPermissionBySysKey(rolePermissionDO.getSysKey());
                    String SelectedContext = userValidateBp.cleanDeletedNode(rolePermissionDO.getSelectedContext(), permission.getSysContext());
                    PermissionVO permissionVO = new PermissionVO();
                    permissionVO.setSysKey(rolePermissionDO.getSysKey());
                    permissionVO.setSysContext(SelectedContext);
                    permissionVOs.add(permissionVO);
                }
                RoleDO roleDo = roleMapper.getRoleByRoleId(lstRoleId.get(i));
                roleVO.roleId = lstRoleId.get(i);
                roleVO.roleName = roleDo.getRoleName();
                roleVO.selectedContext = permissionVOs;
                roleVoList.add(roleVO);
            }
        }
        return VoHelper.getSuccessResult(roleVoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateRolePermission(String operator, List<RoleVO> lstRole) {
        //校验传过来的书架上是否是合法的
        for (RoleVO jumpRoleVO : lstRole) {
            List<PermissionVO> jumpPermissionVOS = jumpRoleVO.selectedContext;
            for (PermissionVO jumpPermissionVO : jumpPermissionVOS){
                //若是没有sys_key , 则返回给前端
                if (StringUtility.isNullOrEmpty(jumpPermissionVO.getSysKey()) ){
                    return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(),"传入的sys_key不能为空");
                }
                //判断传过来的json数据是否能转成SystemRootVO
               if(StringUtility.parseObject(jumpPermissionVO.getSysContext(),SystemRootVO.class) == null){
                   return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(),"传入的数据结构非法");
               }
            }
        }
        //1.首先拿到当前角色的所有的用户 , 首先判断用户是否是管理员,若是管理员则,具有更新数据的权限,否则没有权限
        Map dataMap = new HashMap();
        //判断用户是否是普通用户
        if (!roleMapper.isAdminOrSuperAdmin(operator)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "您不是管理员,没有权限更新数据");
        }
        //如果是超级管理员,则更新所有,否则只能更新自己的创建的
        List<String> lstRoleId = new ArrayList<>();
        for (RoleVO roleVO : lstRole) {
            //判断如果用户不是超级管理员,那么如果他拿到的roleVO 的权限的创建人不是他自己的话,则无权限更新此数据,跳过处理
            if (!roleMapper.isSuperAdminAccount(operator)) {
                RoleDO roleDO = roleMapper.getRoleByRoleId(roleVO.getRoleId());
                if (!operator.equals(roleDO.getCreateBy())) {
                    continue;
                }
            }
            UserRoleDO userRole = new UserRoleDO();
            userRole.setRoleId(Long.valueOf(roleVO.getRoleId()));
            //2. 更新角色的功能权限
            List<PermissionVO> permissionVOS = roleVO.selectedContext;
            List<Long> roleIds = new ArrayList<>();
            List<RolePermissionDO> permissionDOS = new ArrayList<>();
            for (PermissionVO permissionVO : permissionVOS) {
                RolePermissionDO rolePermissionDO = new RolePermissionDO();
                //将功能版本放入do中 ,通过roleId来更新角色的功能权限, 先删除,在插入
                rolePermissionDO.setRoleId(userRole.getRoleId());
                rolePermissionDO.setSysKey(permissionVO.getSysKey());
                rolePermissionDO.setModifiedBy(operator);
                rolePermissionDO.setCreateBy(operator);
                rolePermissionDO.setCreateTime(StringUtility.getDateTimeNow());
                rolePermissionDO.setModifiedTime(StringUtility.getDateTimeNow());
                rolePermissionDO.setSelectedContext(permissionVO.getSysContext());
                //将roleId 装载进list
                roleIds.add(userRole.getRoleId());
                permissionDOS.add(rolePermissionDO);
            }
            rolePermissionMapper.deleteBatch(roleIds);
            logger.info("清理相关的功能权限完成");
            rolePermissionMapper.insertBatch(permissionDOS);
            logger.info("更新相关功能权限完成");
            // 将roleId 放入到 list里面, 用于后面更新缓存
            lstRoleId.add(roleVO.getRoleId());
        }
        dataMap.put("roleIds", lstRoleId);
        /*3、获取roleIds角色对应的用户名*/
        logger.info(String.format("获取的角色id为%s", lstRoleId));
        if (lstRoleId.size() == 0) {
            logger.info("roleID 的集合为空");
           return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), "没有任何数据可以更新");
        }
        List<String> userNames = userRoleMapper.listUserNamesByRoleIds(dataMap);
        logger.info(String.format("获取的用户名为%s", userNames));
        /*4、更新用户操作权限冗余表和缓存*/
        permitStatBp.updateUserPermitCache(userNames);
        return VoHelper.getSuccessResult();
    }

    /**
     * Description: 1、分配权限--获取多个角色已有的用户 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:01
     * @see
     */
    @Override
    public ResultVO getRoleUser(String operator, List<String> lstRoleId) {
		/* 非管理员只能查看自己创建的角色 */
        List<RoleVO> roleList = new ArrayList<>();
        if (lstRoleId != null && lstRoleId.size() > 0) {
            for (int i = 0; i < lstRoleId.size(); i++) {
                RoleDO roleDO = roleMapper.getRoleByRoleId(lstRoleId.get(i));
                if (roleDO != null) {
                    RoleVO roleVO = new RoleVO();
                    roleVO.setRoleName(roleDO.getRoleName());
                    roleVO.setRoleId(roleDO.getRoleId().toString());
                    UserRoleDO userRoleDO = new UserRoleDO();
                    userRoleDO.setRoleId(Long.parseLong(lstRoleId.get(i)));
                    if (!roleMapper.isSuperAdminAccount(operator)) {
                        userRoleDO.setCreateBy(operator);
                    }
                    List<String> lstUserName = userRoleMapper.getUserNameByRoleId(userRoleDO);
                    roleVO.setLstUserName(lstUserName);
                    roleList.add(roleVO);
                }
            }

        }
		/* 查询用户角色关系表 */
        return VoHelper.getSuccessResult(roleList);
    }

    /**
     * Description: 1、分配权限--同时更新多个角色的用户
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:02
     * @see
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateUsersOfRole(List<RoleVO> lstRole, String operator) {
        if (lstRole != null && lstRole.size() > 0) {
            for (int i = 0; i < lstRole.size(); i++) {
                RoleVO roleVO = lstRole.get(i);
                RoleDO roleDO = roleMapper.getRoleByRoleId(roleVO.getRoleId());
                if(roleDO!=null){
                	
                	UserRoleDO userRole = new UserRoleDO();
                	List<UserRoleDO> userRoleDOS = new ArrayList<>();
                	List<String> userNameList = roleVO.getLstUserName();
                	userRole.setRoleId(Long.valueOf(roleVO.getRoleId()));
                	List<UserDO> userList = userMapper.getUserByRoleId(userRole);
                	if (roleMapper.isSuperAdminAccount(operator)) {
                		userRoleMapper.deleteUserRole(userRole);
                		if (userList != null && userList.size() > 0) {
                			for (int q = 0; q < userList.size(); q++) {
                				permitStatBp.updateUserPermitCache(userList.get(q).getUserName());
                			}
                		}
                		for (int j = 0; j < userNameList.size(); j++) {
                			UserRoleDO userRoleDO = new UserRoleDO();
                			userRoleDO.setUserName(userNameList.get(j));
                			userRoleDO.setRoleId(Long.valueOf(roleVO.getRoleId()));
                			userRoleDO.setCreateBy(operator);
                			userRoleDO.setCreateTime(new Date());
                			userRoleDO.setModifiedBy(operator);
                			userRoleDO.setModifiedTime(new Date());
                			userRoleDOS.add(userRoleDO);
                		}
                	} else {
                		if (roleDO.getCreateBy().equals(operator)) {
                			userRole.setCreateBy(operator);
                			userRoleMapper.deleteUserRole(userRole);
                			if (userList != null && userList.size() > 0) {
                				for (int q = 0; q < userList.size(); q++) {
                					permitStatBp.updateUserPermitCache(userList.get(q).getUserName());
                				}
                			}
                			for (int j = 0; j < userNameList.size(); j++) {
                				UserRoleDO userRoleDO = new UserRoleDO();
                				userRoleDO.setUserName(userNameList.get(j));
                				userRoleDO.setRoleId(Long.valueOf(roleVO.getRoleId()));
                				userRoleDO.setCreateBy(operator);
                				userRoleDO.setCreateTime(new Date());
                				userRoleDO.setModifiedBy(operator);
                				userRoleDO.setModifiedTime(new Date());
                				userRoleDOS.add(userRoleDO);
                			}
                		}
                	}
                	if (userRoleDOS != null && userRoleDOS.size() > 0) {
                		userRoleMapper.insertBatch(userRoleDOS);
                		for (int j = 0; j < userRoleDOS.size(); j++) {
                			permitStatBp.updateUserPermitCache(userRoleDOS.get(j).getUserName());
                		}
                		
                	}
                }
            }

        }
        return VoHelper.getSuccessResult();
    }

    /**
     * Description: 1、复制角色 复制角色将创建一个新的角色，并将原角色的权限自动授予给新角色
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:04
     * @see
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyRole(final String operator, String newRoleName, String sourceRoleId) {
        if (StringUtils.isBlank(newRoleName)) {
            throw new URCBizException(ErrorCode.E_000002);
        }
        if (StringUtils.isBlank(sourceRoleId)) {
            throw new URCBizException(ErrorCode.E_000002);
        }
		/* 非admin用户只能管理自己创建的角色 */
        long roleId = 0;
        try {
            roleId = Long.parseLong(sourceRoleId);
        } catch (NumberFormatException e) {
            throw new URCBizException(ErrorCode.E_000003);
        }
        Date currentDate = new Date();
        RoleDO roleDO = getRoleInfo(operator, newRoleName, roleId);
        // 复制角色信息
        roleDO.setRoleId(seqBp.getNextRoleId());
        roleDO.setRoleName(newRoleName);
        roleDO.setCreateTime(currentDate);
        roleDO.setCreateBy(operator);
        roleDO.setModifiedBy(operator);
        roleDO.setModifiedTime(currentDate);
        roleMapper.insert(roleDO);

		/* 复制对应的角色功能权限关系 */
        RolePermissionDO rolePermissionDO = new RolePermissionDO();
        rolePermissionDO.setRoleId(roleId);
        List<RolePermissionDO> rolePermissions = rolePermissionMapper.getRolePermission(rolePermissionDO);
        //如果权限列表为空，则不新增权限列表
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return;
        }
        //保存角色的功能权限
        List<RolePermissionDO> records = new ArrayList<>();
        rolePermissions.stream().forEach(rpDO -> {
            RolePermissionDO record = new RolePermissionDO();
            record.setRoleId(roleDO.getRoleId());
            record.setSysKey(rpDO.getSysKey());
            record.setSelectedContext(rpDO.getSelectedContext());
            record.setCreateTime(currentDate);
            record.setCreateBy(operator);
            record.setModifiedBy(operator);
            record.setModifiedTime(currentDate);
            records.add(record);
        });
        rolePermissionMapper.insertBatch(records);
    }

    /**
     * 判断当前操作者权限,再获取被复制角色信息 1.管理员用户可以复制所有角色信息 2.普通用户只能复制自己创建的角色信息
     */

    private RoleDO getRoleInfo(String operator, String newRoleName, long sourceRoleId) {
        if (roleMapper.checkDuplicateRoleName(newRoleName, null)) {
            throw new URCBizException(ErrorCode.E_101001);
        }
        // 判断当前被复制角色是否为当前用户创建的角色
        RoleDO roleDO = roleMapper.getRoleByRoleId(String.valueOf(sourceRoleId));
        // 判断当前用户是否为管理员用户
        if (roleMapper.isSuperAdminAccount(operator) || operator.equals(roleDO.getCreateBy())) {
            return roleDO;
        }
        throw new URCBizException(ErrorCode.E_101002);
    }

    @Override
    public ResultVO<Integer> checkDuplicateRoleName(String operator, String newRoleName, String roleId) {
        return VoHelper.getSuccessResult(roleMapper.checkDuplicateRoleName(newRoleName, roleId) ? 1 : 0);
    }


    @Autowired
    ISessionBp sessionBp;
    @Autowired
    PermissionMapper permitMapper;
    @Autowired
    private IRolePermissionMapper rolePermitMapper;

    @Override
    @Transactional
    public ResultVO assignAllPermit2Role() {
        Long roleId = sessionBp.getLong("roleId");
        RoleDO roleFromDb = roleMapper.getRoleByRoleId(String.valueOf(roleId));
        if (roleFromDb == null) throw new URCBizException(String.format("roleId:%s不存在", roleId), ErrorCode.E_000000);
        List<RolePermissionDO> lstRolePermit = new ArrayList<>();
        List<PermissionDO> lstPermit = permitMapper.getAllSysPermit();
        if (lstPermit != null && lstPermit.size() > 0) {
            for (PermissionDO permit : lstPermit) {
                RolePermissionDO rp = new RolePermissionDO();
                rp.setRoleId(roleId);
                rp.setSysKey(permit.getSysKey());
                rp.setSelectedContext(permit.getSysContext());
                rp.setCreateTime(new Date());
                rp.setCreateBy(sessionBp.getOperator());
                rp.setModifiedBy(sessionBp.getOperator());
                rp.setModifiedTime(rp.getCreateTime());
                lstRolePermit.add(rp);
            }
            rolePermitMapper.deleteByRoleId(roleId);
            rolePermitMapper.insertBatch(lstRolePermit);
        }
        // 更新角色下的用户的权限缓存
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(roleId);
        List<String> lstUserName = userRoleMapper.getUserNameByRoleId(userRoleDO);
        permitStatBp.updateUserPermitCache(lstUserName);
        return VoHelper.getSuccessResult();
    }

    @Override
    public void handleExpiredRole() {
        try {
            // 获取所有过期的角色关联的用户
            List<String> lstUserName = roleMapper.getUsersOfAllExpiredRole();
            if (lstUserName != null && lstUserName.size() > 0) {
                // 将所有过期的角色 is_active 设置为0
                List<RoleDO> lstRole = roleMapper.updateAllExpiredRole();
                operationBp.addLog(logger.getName(), String.format("设置角色过期:%s", StringUtility.toJSONString_NoException(lstRole)), null);

                // 更新用户的权限：冗余表、缓存
                permitStatBp.updateUserPermitCache(lstUserName);
            } else {
                operationBp.addLog(logger.getName(), "没有角色过期", null);
            }
        } catch (Exception ex) {
            operationBp.addLog(logger.getName(), "处理过期角色ERROR", ex);
        }
    }

    @Override
    public List<SystemRootVO> getUserAuthorizablePermission(String userName) {
        // TODO Auto-generated method stub
        return null;
    }

}
