package com.yks.urc.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.mapper.IUserPermitStatMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserPermissionCacheMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    private IUserPermissionCacheMapper permissionCacheMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private ISessionBp sessionBp;

    @Autowired
    IPermitStatBp permitStatBp;

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
                    // 将API 前缀置为null. 在存入sysContext
                    root.apiUrlPrefix = null;
                    p.setSysContext(StringUtility.toJSONString_NoException(root));
                    p.setCreateTime(new Date());
                    p.setModifiedTime(new Date());
                    p.setCreateBy(sessionBp.getOperator());
                    p.setModifiedBy(sessionBp.getOperator());
                    lstPermit.add(p);
                }
//				permissionMapper.deleteSyspermitDefine(lstPermit);
//				permissionMapper.insertSysPermitDefine(lstPermit);

                // 更新缓存
                for (PermissionDO p : lstPermit) {
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

                }
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
        return rslt;
    }

    @Override
    public ResultVO getUserAuthorizablePermission(String userName) {
        List<PermissionVO> permissionVOs = new ArrayList<PermissionVO>();
        if (roleMapper.isSuperAdminAccount(userName)) {
            //查询所有的角色功能权限
            List<PermissionDO> lstSysKey = permissionMapper.getAllSysKey();
            for (PermissionDO permission : lstSysKey) {
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(permission.getSysKey());
                permissionVO.setSysContext(permission.getSysContext());
                permissionVOs.add(permissionVO);
            }
        } else {
            //业务管理员
            List<String> lstSysKey = userRoleMapper.getSysKeyByUser(userName);
            if (lstSysKey != null && lstSysKey.size() > 0) {
                lstSysKey.remove("004");
                for (String sysKey : lstSysKey) {
                    // 获取用户sys的功能权限json
                    List<String> lstFuncJson = roleMapper.getBizAdminFuncJsonByUserAndSysKey(userName, sysKey);
                    if (lstFuncJson != null && lstFuncJson.size() > 0) {
                        // 合并json树
                        SystemRootVO rootVO = userValidateBp.mergeFuncJson2Obj(lstFuncJson);
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
        if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
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
    public ResultVO deleteSysPermitNode(List<FuncTreeVO> funcTreeVOS) {
        //获取所有和sysKey 有关的角色和权限
        try {
            funcTreeVOS.forEach(funcTreeVO -> {

                List<RolePermissionDO> insertPermissionDO = new ArrayList<>();
                List<Long> roleIds = new ArrayList<>();

                if (StringUtils.isEmpty(funcTreeVO.sysKey)) {
                    return;
                }
                //拿到所有和当前系统有关的角色和权限
                List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.getROlePermissionBySysKey(Long.valueOf(funcTreeVO.sysKey));
                if (CollectionUtils.isEmpty(rolePermissionDOS)) {
                    return;
                }

                rolePermissionDOS.forEach(rolePermissionDO -> {
                    if (StringUtils.isEmpty(rolePermissionDO.getSelectedContext())) {
                        return;
                    }
                    //遍历 json 树,删除节点
                    SystemRootVO systemRootVO = StringUtility.parseObject(rolePermissionDO.getSelectedContext(), SystemRootVO.class);
                    if (systemRootVO == null) {
                        logger.error("json 树转换失败", rolePermissionDO.getSelectedContext());
                        return;
                    }
                    //遍历删除menu
                    this.foreachMenuTree(systemRootVO, funcTreeVO.delKeys);

                    if (rolePermissionDO.getRoleId() != null) {
                        roleIds.add(rolePermissionDO.getRoleId());
                    }
                    //节点删除后,在转化为 json 权限树.入库
                    String selectedContext = StringUtility.toJSONString(systemRootVO);
                    if (StringUtils.isEmpty(selectedContext)) {
                        logger.error("systemRootVＯ　转 json 失败");
                    }
                    rolePermissionDO.setSelectedContext(selectedContext);
                    insertPermissionDO.add(rolePermissionDO);
                });
                updateRolePermissionAndRoleUser(insertPermissionDO, roleIds);

            });
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("删除节点失败,更新权限出错", e.getMessage());
            return VoHelper.getErrorResult();
        }
    }

    /**
     * 更新角色的权限,角色下的用户及缓存
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 17:22
     */
    private void updateRolePermissionAndRoleUser(List<RolePermissionDO> insertPermissionDO, List<Long> roleIds) {
        //清除掉这些角色的权限,重新将权限树插入
        if (!CollectionUtils.isEmpty(insertPermissionDO)) {
            rolePermissionMapper.deleteBatch(roleIds);
            logger.info("清理角色权限完成");
            rolePermissionMapper.insertAndUpdateBatch(insertPermissionDO);
            logger.info("更新相关功能权限完成");
            Map dataMap = new HashMap();
            dataMap.put("roleIds", roleIds);
        /*3、获取roleIds角色对应的用户名*/
            logger.info(String.format("获取的角色id为%s", roleIds));
            if (CollectionUtils.isEmpty(roleIds)) {
                logger.info("roleID 的集合为空");
            }
            List<String> userNames = userRoleMapper.listUserNamesByRoleIds(dataMap);
            logger.info(String.format("获取的用户名为%s", userNames));
    /*4、更新用户操作权限冗余表和缓存*/
            permitStatBp.updateUserPermitCache(userNames);
        }
    }

    /**
     * 遍历 清理module
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 14:38
     */
    private void foreachModuleTree(List<ModuleVO> lstModule, List<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(lstModule)) {
            return;
        }
        for (int i = 0; i < lstModule.size(); i++) {
            if (lstDeleteKey.contains(lstModule.get(i).key)) {
                lstModule.remove(i);
                i--;
            }
            foreachModuleTree(lstModule, lstDeleteKey);
            foreachFuncTree(lstModule.get(i).function, lstDeleteKey);
        }
    }

    /**
     * 遍历 清楚func 节点
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 14:57
     */
    private void foreachFuncTree(List<FunctionVO> functionVOS, List<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(functionVOS)) {
            return;
        }
        for (int i = 0; i < functionVOS.size(); i++) {
            if (lstDeleteKey.contains(functionVOS.get(i).key)) {
                functionVOS.remove(i);
                i--;
            }
            foreachFuncTree(functionVOS.get(i).function, lstDeleteKey);
        }
    }

    /**
     * 遍历所有的menu
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 11:19
     */
    private void foreachMenuTree(SystemRootVO systemRootVO, List<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(systemRootVO.menu)) {
            return;
        }
        for (int i = 0; i < systemRootVO.menu.size(); i++) {
            if (lstDeleteKey.contains(systemRootVO.menu.get(i).key)) {
                systemRootVO.menu.remove(i);
                i--;
            } else {
                foreachModuleTree(systemRootVO.menu.get(i).module, lstDeleteKey);
            }
        }
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
    public ResultVO updateSysPermitNode(List<FuncTreeVO> funcTreeVOS) {
        // 找到和sysKey 相关的 权限
        try {
            funcTreeVOS.forEach(funcTreeVO -> {

                List<RolePermissionDO> insertPermissionDO = new ArrayList<>();
                List<Long> roleIds = new ArrayList<>();

                if (StringUtils.isEmpty(funcTreeVO.sysKey)) {
                    return;
                }
                //拿到所有和当前系统有关的角色和权限
                List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.getROlePermissionBySysKey(Long.valueOf(funcTreeVO.sysKey));
                if (CollectionUtils.isEmpty(rolePermissionDOS)) {
                    return;
                }
                rolePermissionDOS.forEach(rolePermissionDO -> {
                    if (StringUtils.isEmpty(rolePermissionDO.getSelectedContext())) {
                        return;
                    }
                    //遍历 json 树, 修改节点
                    SystemRootVO systemRootVO = StringUtility.parseObject(rolePermissionDO.getSelectedContext(), SystemRootVO.class);
                    if (systemRootVO == null) {
                        logger.error("json 树转换失败", rolePermissionDO.getSelectedContext());
                        return;
                    }
                    //修改节点
                    updateMenuValue(systemRootVO, funcTreeVO.updateNode);
                    //将得到的结果再转为json
                    String selectedContext = StringUtility.toJSONString(systemRootVO);
                    if (StringUtils.isEmpty(selectedContext)) {
                        logger.error("systemRootVＯ　转 json 失败");
                    }
                    rolePermissionDO.setSelectedContext(selectedContext);
                    insertPermissionDO.add(rolePermissionDO);
                });
                updateRolePermissionAndRoleUser(insertPermissionDO, roleIds);
            });
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("删除节点失败,更新权限出错", e.getMessage());
            return VoHelper.getErrorResult();
        }
    }

    /**
     * 更新菜单下的节点
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 17:11
     */
    private void updateMenuValue(SystemRootVO systemRootVO, List<NodeVO> lstNode) {
        if (systemRootVO == null || CollectionUtils.isEmpty(lstNode) || CollectionUtils.isEmpty(systemRootVO.menu)) {
            return;
        }
        systemRootVO.menu.forEach(menuVO -> {
            lstNode.forEach(nodeVO -> {
                if (StringUtils.isEmpty(nodeVO.key)) {
                    return;
                }
                //当key相等时,修改数据
                if (nodeVO.key.equalsIgnoreCase(menuVO.key)) {
                    if (StringUtils.isNotEmpty(nodeVO.name)) {
                        menuVO.name = nodeVO.name;
                    }
                    if (StringUtils.isNotEmpty(nodeVO.url)) {
                        menuVO.url = nodeVO.url;
                    }
                }
                updateModuleValue(menuVO.module, lstNode);
            });
        });
    }

    /**
     * 更新模块下的节点
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 17:12
     */
    private void updateModuleValue(List<ModuleVO> lstModule, List<NodeVO> lstNode) {
        if (CollectionUtils.isEmpty(lstModule) || CollectionUtils.isEmpty(lstNode)) {
            return;
        }
        lstModule.forEach(moduleVO -> {
            lstNode.forEach(nodeVO -> {
                if (StringUtils.isEmpty(nodeVO.key)) {
                    return;
                }
                //当key相等时,修改数据
                if (nodeVO.key.equalsIgnoreCase(moduleVO.key)) {
                    if (StringUtils.isNotEmpty(nodeVO.name)) {
                        moduleVO.name = nodeVO.name;
                    }
                    if (StringUtils.isNotEmpty(nodeVO.url)) {
                        moduleVO.url = nodeVO.url;
                    }
                    updateModuleValue(moduleVO.module, lstNode);
                    updateFuncValue(moduleVO.function, lstNode);
                }
            });
        });
    }

    /**
     * 更新功能下的节点
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 17:19
     */
    private void updateFuncValue(List<FunctionVO> lstFunction, List<NodeVO> lstNode) {
        if (CollectionUtils.isEmpty(lstFunction) || CollectionUtils.isEmpty(lstNode)) {
            return;
        }
        lstFunction.forEach(functionVO -> {
            lstNode.forEach(nodeVO -> {
                if (StringUtils.isEmpty(nodeVO.key)) {
                    return;
                }
                //当key相等时,修改数据
                if (nodeVO.key.equalsIgnoreCase(functionVO.key)) {
                    if (StringUtils.isNotEmpty(nodeVO.name)) {
                        functionVO.name = nodeVO.name;
                    }
                    updateFuncValue(functionVO.function, lstNode);
                }
            });
        });
    }
}
