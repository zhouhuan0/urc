package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.cache.bp.api.IUpdateAffectedUserPermitCache;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.entity.UserPermitStatDO;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.funcjsontree.bp.api.IFuncJsonTreeBp;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.*;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.service.api.IPermissionService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermissionServiceImpl implements IPermissionService {

    private Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Value("${importSysPermit.aesPwd}")
    private String aesPwd;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    private IUserRoleMapper userRoleMapper;

    @Autowired
    IRoleMapper roleMapper;

    @Autowired
    IRolePermissionMapper rolePermissionMapper;

    @Autowired
    private IUserPermitStatMapper userPermitStatMapper;

    @Autowired
    IOperationBp operationBp;

    @Autowired
    ICacheBp cacheBp;

    @Autowired
    private IUserMapper iUserMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private ISessionBp sessionBp;

    @Autowired
    IPermitStatBp permitStatBp;

    @Autowired
    IFuncJsonTreeBp funcJsonTreeBp;

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUpdateAffectedUserPermitCache updateAffectedUserPermitCache;

    @Transactional
    @Override
    public ResultVO importSysPermit(String jsonStr) {
        ResultVO rslt = new ResultVO();
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            //获取操作人
            String operator = jsonObject.get("operator").toString();
            //获取密码
            String pwd = jsonObject.get("pwd").toString();
            //获取数据
            String data = jsonObject.get("data").toString();
            if (StringUtility.isNullOrEmpty(operator)) {
                return VoHelper.getErrorResult();
            }
            //判断密码是否相等
            if (!aesPwd.equals(pwd)) {
                return VoHelper.getErrorResult();
            }
            SystemRootVO[] arr = StringUtility.parseObject(data, new SystemRootVO[0].getClass());
            // 若是key重复，则不让推
            List<String>   keys =new ArrayList<>();
            if (this.duplicateKey(arr, keys)) {
                if (!CollectionUtils.isEmpty(keys)) {
                    //找出重复元素
                    Set<String> duplicateKeys = keys.stream().filter(key -> Collections.frequency(keys, key) > 1).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(duplicateKeys)) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.HANDLE_DATA_EXCEPTION.getCode(), String.format("推送的菜单树里有重复的key值，keys =%s", duplicateKeys.toString()));
                    }
                }
            }
            if (arr != null && arr.length > 0) {
                List<PermissionDO> lstPermit = new ArrayList<>(arr.length);
                for (SystemRootVO root : arr) {
                    if (root.apiUrlPrefix == null || root.apiUrlPrefix.size() == 0) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "apiUrlPrefix 不能为空");
                    }
                    PermissionDO p = new PermissionDO();
                    p.setApiUrlPrefixJson(StringUtility.toJSONString_NoException(root.apiUrlPrefix));
                    p.setSysName(root.system.name);
                    p.setSysKey(root.system.key);
                    p.setSysType(root.system.sysType == null ? UrcConstant.SysType.ERP : root.system.sysType );
                    // 将API 前缀置为null. 在存入sysContext
                    root.apiUrlPrefix = null;
                    p.setSysContext(StringUtility.toJSONString_NoException(root));
                    p.setCreateTime(new Date());
                    p.setModifiedTime(new Date());
                    p.setCreateBy(sessionBp.getOperator());
                    p.setModifiedBy(sessionBp.getOperator());
                    lstPermit.add(p);
                }
                // 更新缓存
                for (PermissionDO p : lstPermit) {
                    //更新定义表
                    if (p==null){
                        continue;
                    }
                    PermissionDO pFromDb = permissionMapper.getPermission(p.getSysKey());
                    if (pFromDb == null) {
                        // insert
                        permissionMapper.insertSysPermitDefine(Arrays.asList(p));
                    } else {
                        // update
                        permissionMapper.updateSysContextBySysKey(p);
                    }
                    //更新缓存
                    cacheBp.insertSysContext(p.getSysKey(), p.getSysContext());
                    //更新API前缀
                    this.updateApiPrefixCache();
                   /* //更新角色的权限, 角色下的用户 和缓存
                    if (updateRolePermissionAndCache(p)){ continue;}*/

                }
                permitRefreshTaskBp.addPermitRefreshTaskForImportSysPermit(Arrays.asList(arr).stream().map(c->c.system.key).collect(Collectors.toList()));
                operationBp.addLog(PermissionServiceImpl.class.getName(), String.format("导入功能权限:%s", data), null);
                rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
                rslt.msg = "推送成功";
            } else {
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                rslt.msg = "没有 data";
            }
        } catch (Exception ex) {
            logger.error(String.format("importSysPermit:%s", jsonStr), ex);
            throw new URCBizException(ex.getMessage(), ErrorCode.E_000000);
        }
        //推送菜单树完成 将超级管理员存入urc_role_user_affected 调assignAllPermit2Role()刷新超级管理员权限
        saveSuperAdministrator();
        return rslt;
    }

    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;

    /**
     *    重复key值
     * @param
     * @return
     * @Author lwx
     * @Date 2019/4/27 10:00
     */
    private Boolean duplicateKey(SystemRootVO[] arr,List<String> keys) {
        if (arr == null || arr.length == 0 || keys ==null ) {
            return true;
        }
        for (int i = 0,size =arr.length; i <size ; i++) {
            SystemRootVO systemRootVO = arr[i];
            if (systemRootVO == null || CollectionUtils.isEmpty(systemRootVO.menu)){
                return true;
            }
            systemRootVO.menu.forEach(menuVO -> {
                if(StringUtils.isNotEmpty(menuVO.key)) {
                    keys.add(menuVO.key);
                }
                if (CollectionUtils.isEmpty(menuVO.module)){
                    return;
                }
                this.foreachModule(menuVO.module,keys);
            });
        }
        return true;
    }

    private void foreachModule(List<ModuleVO> module,List<String> keys){
        if (CollectionUtils.isEmpty(module) || keys ==null){
            return;
        }

        module.forEach(moduleVO -> {
            if (StringUtils.isNotEmpty(moduleVO.key)){
                keys.add(moduleVO.key);
            }
            if (!CollectionUtils.isEmpty(moduleVO.function)){
                this.foreachModule(moduleVO.module,keys);
            }
            if (!CollectionUtils.isEmpty(moduleVO.function)) {
                this.foreachFunction(moduleVO.function,keys);
            }
        });

    }

    private void foreachFunction(List<FunctionVO> function,List<String> keys){
        if (CollectionUtils.isEmpty(function)|| keys ==null){
            return;
        }
        function.forEach(functionVO -> {
            if (StringUtils.isNotEmpty(functionVO.key)){
                keys.add(functionVO.key);
            }
            if (!CollectionUtils.isEmpty(functionVO.function)){
                foreachFunction(functionVO.function,keys);
            }
        });
    }

    /**
     * 推送菜单树时将超级管理员存入urc_role_user_affected
     */
    private void saveSuperAdministrator() {
    	
    	try {
    		Long roleId = roleMapper.selectAllSuperAdministrator();
            updateAffectedUserPermitCache.assignAllPermit2SuperAdministrator(roleId);
		} catch (Exception e) {
			logger.error("saveSuperAdministrator error! {}",e);
		}
    }

    @Override
    public ResultVO getUserAuthorizablePermission(String userName,String sysType) {
        List<PermissionVO> permissionVOs = new ArrayList<PermissionVO>();
        if (roleMapper.isSuperAdminAccount(userName)) {
            List<PermissionDO> lstSysKey = null;
            //查询所有的角色功能权限
            if(StringUtils.isEmpty(sysType)) {
                 lstSysKey = permissionMapper.getAllSysKey();
            }else{
                lstSysKey = permissionMapper.getSysKey(sysType);
            }
            for (PermissionDO permission : lstSysKey) {
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(permission.getSysKey());
                permissionVO.setSysContext(permission.getSysContext());
                permissionVOs.add(permissionVO);
            }
        } else {
            //查询用户是否拥有系统功能管理员
            List<PermissionDO> userAuthorizablePermissionForPosition = null;
            if(StringUtils.isEmpty(sysType)) {
                userAuthorizablePermissionForPosition = iUserMapper.getUserAuthorizablePermissionForPosition(userName);
            }else {
                userAuthorizablePermissionForPosition = iUserMapper.getUserPermission(userName, sysType);
            }
            for (PermissionDO permissionDO : userAuthorizablePermissionForPosition) {
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(permissionDO.getSysKey());
                permissionVO.setSysContext(permissionDO.getSysContext());
                permissionVOs.add(permissionVO);
            }
            List<String> collect = userAuthorizablePermissionForPosition.stream().map(PermissionDO::getSysKey).collect(Collectors.toList());
            //兼容角色部分功能权限
            List<String> lstSysKey = null;
            if(StringUtils.isEmpty(sysType)) {
                 lstSysKey = userRoleMapper.getSysKeyByUser(userName);
            }else{
                lstSysKey = userRoleMapper.getSysKeyByUserAndType(userName,sysType);
            }
            //去除是功能管理员的系统
            lstSysKey.removeAll(collect);
            if (lstSysKey != null && lstSysKey.size() > 0) {
//                lstSysKey.remove("004");
                for (String sysKey : lstSysKey) {
                    // 获取用户sys的功能权限json (查询是业务管理员的权限)
                    List<String> lstFuncJson = roleMapper.getBizAdminFuncJsonByUserAndSysKey(userName, sysKey);
                    if (lstFuncJson != null && lstFuncJson.size() > 0) {
                        // 合并json树
                        SystemRootVO rootVO = userValidateBp.mergeFuncJson2Obj(lstFuncJson);
                        if(null == rootVO) continue;
                        // 只有超管能分配用户中心权限
                        if (StringConstant.URC_SYS_KEY.equalsIgnoreCase(sysKey)) {
                            Optional<MenuVO> op = rootVO.menu.stream().filter(c -> StringUtility.stringEqualsIgnoreCase(c.key, StringConstant.URC_PERMIT_KEY)).findFirst();
                            if (op.isPresent()) {
                                rootVO.menu.remove(op.get());
                            }
                        }
                        PermissionVO permissionVO = new PermissionVO();
                        permissionVO.setSysKey(sysKey);
                        permissionVO.setSysContext(StringUtility.toJSONString_NoException(rootVO));
                        permissionVOs.add(permissionVO);
                    }
                }
            }
        }
        return VoHelper.getSuccessResult(permissionVOs);
    }

    /**
     * Description: 查看功能权限--获取一个用户所有应用的功能权限（分页）
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/18 23:34
     * @see
     */
    @Override
    public ResultVO getUserPermissionList(String jsonStr) {
         /*1、json字符串转json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、请求参数的基本校验并转换为内部使用的Map*/
        Map<String, Object> queryMap = new HashMap<>();
        checkAndConvertParam(queryMap, jsonObject);
        /*3、查询数据权限模板列表信息*/
        List<UserPermitStatDO> userPermitStatDOS = userPermitStatMapper.listUserPermitStatsByPage(queryMap);
        /*4、List<DO> 转 List<VO>*/
        List<UserPermitStatVO> userPermitStatVOs = convertUserPermitStatDoToVO(userPermitStatDOS);
        /*5、获取总条数*/
        Long total = userPermitStatMapper.getCounts(queryMap.get("userName").toString());
        PageResultVO pageResultVO = new PageResultVO(userPermitStatVOs, total, queryMap.get("pageSize").toString());
        return VoHelper.getSuccessResult(pageResultVO);
    }
   /* */

    /**
     * 更新缓存API前缀
     *
     * @return
     * @param:
     * @Author lwx
     * @Date 2018/7/17 15:44
     *//*
    @Override
    public ResultVO updateApiPrefixCache() {
        try {
            List<PermissionDO> permissionDOList =permissionMapper.getSysApiUrlPrefix();
            cacheBp.setSysApiUrlPrefix(permissionDOList);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.SUCCESS.getCode(),"缓存API更新成功");
        } catch (Exception e) {
            logger.error("未知异常",e);
           return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"更新失败");
        }
    }*/
    private List<UserPermitStatVO> convertUserPermitStatDoToVO(List<UserPermitStatDO> userPermitStatDOS) {
        List<UserPermitStatVO> userPermitStatVOS = new ArrayList<>();
        for (UserPermitStatDO userPermitStatDO : userPermitStatDOS) {
            UserPermitStatVO userPermitStatVO = new UserPermitStatVO();
            BeanUtils.copyProperties(userPermitStatDO, userPermitStatVO);
            userPermitStatVO.setFuncDesc(userPermitStatDO.getFuncJson());
            userPermitStatVO.setSysName(userPermitStatDO.getPermissionDO().getSysName());
            userPermitStatVOS.add(userPermitStatVO);
        }
        return userPermitStatVOS;
    }

    /**
     * Description: 检查输入参数并转换
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 11:54
     * @see
     */
    private void checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
        String operator = jsonObject.getString("operator");
        String userName = jsonObject.getString("userName");
        if (StringUtility.isNullOrEmpty(operator) || StringUtility.isNullOrEmpty(userName)) {
            throw new URCBizException("parameter operator or userName is null", ErrorCode.E_000002);
        }
        /*operator=userName→ 查看当前用户;operator!=userName→ 查看他人用户   ps:业务管理员或超级管理员才能查看*/
        if (!(operator.trim().equals(userName.trim()))) {
            Boolean isAdmin = roleMapper.isAdminOrSuperAdmin(operator);
            if (!isAdmin) {
                throw new URCBizException(String.format("当前用户【%s】不是业务管理员或超级管理员，不能查看他人操作权限", operator), ErrorCode.E_000003);
            }
        }
        queryMap.put("userName", userName);
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        if (!StringUtility.isNum(pageNumber) || !StringUtility.isNum(pageData)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000003);
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);
    }

    /**
     * 更新API前缀
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/8/15 10:21
     */
    @Override
    public ResultVO updateApiPrefixCache() {
        try {
            List<PermissionDO> permissionDOList = permissionMapper.getSysApiUrlPrefix();
            cacheBp.setSysApiUrlPrefix(permissionDOList);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.SUCCESS.getCode(), "缓存API更新成功");
        } catch (Exception e) {
            logger.error("未知异常", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "更新失败");
        }
    }

    /**
     * 删除功能权限树节点
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 10:33
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysPermitNode(FuncTreeVO funcTreeVO) {
        try {
            return funcJsonTreeBp.deleteSysPermitNode(funcTreeVO);
        } catch (Exception e) {
            logger.error("删除节点失败,失败原因:", e);
        }
        return null;
    }


    /**
     * 修改功能权限节点树
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 15:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysPermitNode(FuncTreeVO funcTreeVO) {
        try {
            return funcJsonTreeBp.updateSysPermitNode(funcTreeVO);
        } catch (Exception e) {
            logger.error("删除节点失败,更新权限出错", e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> getUserAuthorizableSysKey(String operator) {
        //角色只有ERP系统
        ResultVO<List<PermissionVO>> rslt = getUserAuthorizablePermission(operator,"0");
        return rslt.data.stream().map(c -> c.getSysKey()).distinct().collect(Collectors.toList());
    }

}
