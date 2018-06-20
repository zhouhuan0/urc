package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.enums.UserCentralStatusEnum;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.Permission;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.entity.UserRoleDO;
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
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        /*组装查询条件queryMap*/
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("createBy", operator);
        //RoleVO roleVO = StringUtility.parseObject(jsonObject.getString("role"), RoleVO.class);
        RoleVO roleVo = jsonObject.getObject("role", RoleVO.class);
        if (roleVo != null) {
            RoleDO roleDO = new RoleDO();
            BeanUtils.copyProperties(roleVo, roleDO);
            queryMap.put("roleDO", roleDO);
        }
        /*管理员角色不需要createBy条件，可以查看所有的角色*/
        Boolean isAdmin = roleMapper.isAdminAccount(operator);
        if (isAdmin) {
            queryMap.put("createBy", "");
        }

        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);
        List<RoleDO> roleDOS = roleMapper.listRolesByPage(queryMap);
        /* 4、List<DO> 转 List<VO> */
        List<RoleVO> roleVOS = convertDoToVO(roleDOS);
        /* 5、获取总条数 */
        Long total = roleMapper.getCounts(queryMap.get("createBy").toString());
        PageResultVO pageResultVO = new PageResultVO(roleVOS, total, Integer.valueOf(queryMap.get("pageSize").toString()));
        return VoHelper.getSuccessResult(pageResultVO);
    }

    private List<RoleVO> convertDoToVO(List<RoleDO> roleDOS) {
        List<RoleVO> roleVOS = new ArrayList<>();
        for (RoleDO roleDO : roleDOS) {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(roleDO, roleVO);
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        RoleVO roleVO = StringUtility.parseObject(jsonObject.getString("role"), RoleVO.class);
        if (roleVO == null) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        /* 3.判断当前用户是否是管理员——管理员管理员可以直接进行操作 */
        Boolean isAdmin = roleMapper.isAdminAccount(operator);
        if (isAdmin) {
            insertOrUpdateRole(operator, roleVO);
        } else {
            /* 4、非管理员，查询需要操作的角色 */
            RoleDO opRoleDO = roleMapper.getByRoleName(roleVO.getRoleName());
            /* 4.1 非管理员———该角色存在但是创建人不是当前用户 */
            if (opRoleDO != null && !opRoleDO.getCreateBy().equals(operator)) {
                return VoHelper.getErrorResult(UserCentralStatusEnum.No_PERMISSION.getCode(), UserCentralStatusEnum.No_PERMISSION.getDesc());
            } else {
				/* 4.2 非管理员———a.该角色存在且创建人是当前用户；b.该角色不存在 */
                insertOrUpdateRole(operator, roleVO);
            }

        }
        return VoHelper.getSuccessResult();
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
    private void insertOrUpdateRole(String operator, RoleVO roleVO) {
		/* 1、添加或更新角色表 */
        RoleDO roleDO = new RoleDO();
        BeanUtils.copyProperties(roleVO, roleDO);
        Long roleId = seqBp.getNextRoleId();
        roleDO.setRoleId(roleId);
        roleDO.setCreateBy(operator);
        roleDO.setCreateTime(new Date());
        roleDO.setModifiedBy(operator);
        roleDO.setModifiedTime(new Date());
        int rtn = roleMapper.insertOrUpdate(roleDO);
        if (rtn == 1) {
            /*rtn == 1；表示新增角色*/
            /*批量新增角色-操作权限关系数据*/
            insertBatchRolePermission(roleVO, operator, roleDO.getRoleId());
            /*批量新增用户-角色关系数据*/
            insertBatchUserRole(roleVO, operator, roleDO.getRoleId());
        } else {
            /*rtn>1; 表示编辑;需要先删除原有数据，再重新批量添加*/
            /*删除原有角色-权限关系数据*/
            rolePermissionMapper.deleteByRoleId(roleDO.getRoleId());
            /*批量新增角色-操作权限关系数据*/
            insertBatchRolePermission(roleVO, operator, roleDO.getRoleId());
            /*删除原有用户-角色关系数据*/
            userRoleMapper.deleteByRoleId(roleId);
            /*批量新增用户-角色关系数据*/
            insertBatchUserRole(roleVO, operator, roleDO.getRoleId());
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        String roleIdStr = jsonObject.getString("roleId");
        if (!StringUtil.isNum(roleIdStr)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        RoleDO roleDO = roleMapper.getRoleDatasByRoleId(Long.valueOf(roleIdStr));
        if (roleDO == null) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        Boolean isAdmin = roleMapper.isAdminAccount(operator);
        if (!isAdmin && !operator.equals(roleDO.getCreateBy())) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        /*3、将roleDO转为roleVO*/
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(roleDO, roleVO);
        /*4、组装roleVO里的selectedContext*/
        List<RolePermissionDO> rolePermissionDOS = roleDO.getPermissionDOList();
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
        if (!roleMapper.isAdminAccount(operator)) {
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
        if (roleMapper.isAdminAccount(operator)) {
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
        for (int i = 0; i < lstRoleId.size(); i++) {
            RoleVO roleVO = new RoleVO();
            RolePermissionDO permissionDO = new RolePermissionDO();
            permissionDO.setRoleId(Long.parseLong(lstRoleId.get(i)));
            if (!roleMapper.isAdminAccount(operator)) {
                permissionDO.setCreateBy(operator);
            }
            List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRolePermission(permissionDO);
            List<PermissionVO> permissionVOs = new ArrayList<PermissionVO>();
            for (RolePermissionDO rolePermissionDO : rolePermissionList) {
                Permission permission = permissionMapper.getPermissionBySysKey(rolePermissionDO.getSysKey());
                String SelectedContext = userValidateBp.cleanDeletedNode(rolePermissionDO.getSysKey(), permission.getSysContext());
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(rolePermissionDO.getSysKey());
                permissionVO.setSysContext(SelectedContext);
                permissionVOs.add(permissionVO);
            }
            RoleDO roleDo = roleMapper.getRoleByRoleId(Long.parseLong(lstRoleId.get(i)));
            roleVO.roleId = Long.parseLong(lstRoleId.get(i));
            roleVO.roleName = roleDo.getRoleName();
            roleVO.selectedContext = permissionVOs;
            roleVoList.add(roleVO);
        }
        return VoHelper.getSuccessResult(roleVoList);
    }

    @Override
    public ResultVO
    updateRolePermission(String operator, List<RoleVO> lstRole) {
        RolePermissionDO rolePermissionDO = new RolePermissionDO();
        List<String> userNameList = new ArrayList<>();
        if (StringUtility.isNullOrEmpty(operator)) {
            return VoHelper.getErrorResult();
        }
        //1.首先拿到当前角色的所有的用户
        try {
            for (RoleVO roleVO : lstRole) {
                UserRoleDO userRole = new UserRoleDO();
                userRole.setRoleId(roleVO.roleId);
                List<UserDO> userDOList = userMapper.getUserByRoleId(userRole);
                //更新缓存
                for (int i = 0; i < userDOList.size(); i++) {
                    String userName = userDOList.get(i).getUserName();
                    userNameList.add(userName);
                }
                permitStatBp.updateUserPermitCache(userNameList);
                //2. 更新角色的功能权限
                List<PermissionVO> permissionVOS = roleVO.selectedContext;
                for (PermissionVO permissionVO : permissionVOS) {
                    //将功能版本放入do中
                    rolePermissionDO.setSelectedContext(permissionVO.getSysContext());
                    rolePermissionMapper.updateUserRoleByRoleId(rolePermissionDO);
                }
            }
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            return VoHelper.getErrorResult();
        }
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
        for (int i = 0; i < lstRoleId.size(); i++) {
            RoleDO roleDO = roleMapper.getRoleByRoleId(Long.parseLong(lstRoleId.get(i)));
            RoleVO roleVO = new RoleVO();
            roleVO.setRoleName(roleDO.getRoleName());
            roleVO.setRoleId(roleDO.getRoleId());
            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setRoleId(Long.parseLong(lstRoleId.get(i)));
            if (!roleMapper.isAdminAccount(operator)) {
                userRoleDO.setCreateBy(operator);
            }
            List<String> lstUserName = userRoleMapper.getUserNameByRoleId(userRoleDO);
            roleVO.setLstUserName(lstUserName);
            roleList.add(roleVO);
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
        for (int i = 0; i < lstRole.size(); i++) {
            RoleVO roleVO = lstRole.get(i);
            UserRoleDO userRole = new UserRoleDO();
            List<UserRoleDO> userRoleDOS = new ArrayList<>();
            List<String> userNameList = roleVO.getLstUserName();
            userRole.setRoleId(roleVO.getRoleId());
            if (roleMapper.isAdminAccount(operator)) {
                userRoleMapper.deleteUserRole(userRole);
                for (int j = 0; j < userNameList.size(); j++) {
                    UserRoleDO userRoleDO = new UserRoleDO();
                    userRoleDO.setUserName(userNameList.get(i));
                    userRoleDO.setRoleId(roleVO.getRoleId());
                    userRoleDO.setCreateBy(operator);
                    userRoleDO.setCreateTime(new Date());
                    userRoleDO.setModifiedBy(operator);
                    userRoleDO.setModifiedTime(new Date());
                }
            } else {
                userRole.setCreateBy(operator);
                userRoleMapper.deleteUserRole(userRole);
                for (int j = 0; j < userNameList.size(); j++) {
                    UserDO usreDO = userMapper.getUserByUserName(userNameList.get(i));
                    if (usreDO.getCreateBy().equals(operator)) {
                        UserRoleDO userRoleDO = new UserRoleDO();
                        userRoleDO.setUserName(userNameList.get(i));
                        userRoleDO.setRoleId(roleVO.getRoleId());
                        userRoleDO.setCreateBy(operator);
                        userRoleDO.setCreateTime(new Date());
                        userRoleDO.setModifiedBy(operator);
                        userRoleDO.setModifiedTime(new Date());
                    }
                }
            }
            userRoleMapper.insertBatch(userRoleDOS);
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
    public void copyRole(String operator, String newRoleName, String sourceRoleId) {
		/* 非admin用户只能管理自己创建的角色 */
        RoleDO roleDO = getRoleInfo(operator, newRoleName, Long.parseLong(sourceRoleId));
        // 复制角色信息
        roleDO.setRoleId(seqBp.getNextRoleId());
        roleDO.setRoleName(newRoleName);
        roleMapper.insert(roleDO);
		/* 复制对应的角色权功能限关系 */
//		List<RoleDO> rolePermission = getRolePermission(Arrays.asList(sourceRoleId));
        // rolePermission.stream().forEach(role -> {
        // role.getPermissionDO()
        // });
    }

    /**
     * 判断当前操作者权限,再获取被复制角色信息 1.管理员用户可以复制所有角色信息 2.普通用户只能复制自己创建的角色信息
     */

    private RoleDO getRoleInfo(String operator, String newRoleName, long sourceRoleId) {
        if (roleMapper.checkDuplicateRoleName(newRoleName, null)) {
            throw new RuntimeException("角色名已存在");
        }
        // 判断当前被复制角色是否为当前用户创建的角色
        RoleDO roleDO = roleMapper.getRoleByRoleId(sourceRoleId);
        // 判断当前用户是否为管理员用户
        if (roleMapper.isAdminAccount(operator) || operator.equals(roleDO.getCreateBy())) {
            return roleDO;
        }
        throw new RuntimeException("普通用户只能复制自己创建的角色信息");
    }

    @Override
    public ResultVO<Integer> checkDuplicateRoleName(String operator, String newRoleName, String roleId) {
        return VoHelper.getSuccessResult(roleMapper.checkDuplicateRoleName(newRoleName, roleId) ? 1 : 0);
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
