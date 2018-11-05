/**
 * 〈一句话功能简述〉<br>
 * 〈功能权限树相关〉
 *
 * @author lwx
 * @create 2018/11/5
 * @since 1.0.0
 */
package com.yks.urc;

import com.yks.urc.entity.RolePermissionDO;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.funcjsontree.bp.api.IFuncJsonTreeBp;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class IFuncJsonTreeBpImpl implements IFuncJsonTreeBp {
    private static Logger logger = LoggerFactory.getLogger(IFuncJsonTreeBpImpl.class);

    @Autowired
    private IRolePermissionMapper rolePermissionMapper;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private IPermitStatBp permitStatBp;

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
            //递归返回结果
            //受影响的角色id 有
            Boolean result;
            // sysKey, 角色
           List<RolePermissionDO> updatePermissionMap =new ArrayList<>(4);
            for (FuncTreeVO funcTreeVO : funcTreeVOS) {
                if (StringUtils.isEmpty(funcTreeVO.sysKey)) {
                    continue;
                }
                //拿到所有和当前系统有关的角色和权限
                List<RolePermissionDO> updatePermissionS =new ArrayList<>();
                List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.getROlePermissionBySysKey(Long.valueOf(funcTreeVO.sysKey));
                if (CollectionUtils.isEmpty(rolePermissionDOS)) {
                    continue;
                }
                for (RolePermissionDO rolePermissionDO : rolePermissionDOS) {
                    RolePermissionDO updateRolePermission =new RolePermissionDO();
                    if (StringUtils.isEmpty(rolePermissionDO.getSelectedContext())) {
                        continue;
                    }
                    //遍历 json 树,删除节点
                    SystemRootVO systemRootVO = StringUtility.parseObject(rolePermissionDO.getSelectedContext(), SystemRootVO.class);
                    if (systemRootVO == null) {
                        logger.error("json 树转换失败", rolePermissionDO.getSelectedContext());
                        throw new URCBizException("数据转换异常,异常的角色为", String.valueOf(rolePermissionDO.getRoleId()));
                    }
                    //遍历删除menu
                    result = this.foreachMenuTree(systemRootVO, funcTreeVO.delKeys);
                    //节点删除后,在转化为 json 权限树.入库
                    String selectedContext = StringUtility.toJSONString(systemRootVO);
                    if (StringUtils.isEmpty(selectedContext)) {
                        logger.error("systemRootVO　转 json 失败,失败的roleId为[%s]", rolePermissionDO.getRoleId());
                    }
                    if (result == true && rolePermissionDO.getRoleId() !=null) {
                        // 已删除完成, 收集受影响的角色id
                        updateRolePermission.setRoleId(rolePermissionDO.getRoleId());
                        updateRolePermission.setSelectedContext(selectedContext);
                        updateRolePermission.setSysKey(rolePermissionDO.getSysKey());
                        updatePermissionS.add(rolePermissionDO);
                    }
                    //说明key 已经删除完毕
                    if (CollectionUtils.isEmpty(funcTreeVO.delKeys)) {
                        break;
                    }
                }
            }
            updateRolePermissionAndRoleUser(updatePermissionMap);
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
    private void updateRolePermissionAndRoleUser(List<RolePermissionDO> updatePermissions) {
        //更新权限树
        if (CollectionUtils.isEmpty(updatePermissions)) {
            return;
        }
        Map dataMap = new HashMap();
        List<String> roleIds =new ArrayList<>();
        updatePermissions.forEach(rolePermissionDO -> {
            //更新权限树
            rolePermissionMapper.updateUserRoleByRoleId(rolePermissionDO);
            roleIds.add(String.valueOf(rolePermissionDO.getRoleId()));
        });
        //去重
        roleIds.stream().distinct();
        dataMap.put("roleIds",roleIds);
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

    /**
     * 遍历 清理module
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 14:38
     */
    private void foreachModuleTree(List<ModuleVO> lstModule, Set<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(lstModule)) {
            return;
        }
        for (int i = 0; i < lstModule.size(); i++) {
            if (lstDeleteKey.contains(lstModule.get(i).key)) {
                lstModule.remove(i);
                //key 已经删除成功
                lstDeleteKey.remove(lstModule.get(i).key);
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
    private void foreachFuncTree(List<FunctionVO> lstFunction, Set<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(lstFunction)) {
            return;
        }
        for (int i = 0; i < lstFunction.size(); i++) {
            if (lstDeleteKey.contains(lstFunction.get(i).key)) {
                lstFunction.remove(i);
                //移除
                lstDeleteKey.remove(lstFunction.get(i).key);
                i--;
            }
            foreachFuncTree(lstFunction.get(i).function, lstDeleteKey);
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
    private Boolean foreachMenuTree(SystemRootVO systemRootVO, Set<String> lstDeleteKey) {
        if (CollectionUtils.isEmpty(systemRootVO.menu)) {
            return false;
        }
        for (int i = 0; i < systemRootVO.menu.size(); i++) {
            if (lstDeleteKey.contains(systemRootVO.menu.get(i).key)) {
                systemRootVO.menu.remove(i);
                //key 也移除掉
                lstDeleteKey.remove(systemRootVO.menu.get(i).key);
                i--;
            } else {
                foreachModuleTree(systemRootVO.menu.get(i).module, lstDeleteKey);
            }
        }
        return true;
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
            List<RolePermissionDO> updatePermissions = new ArrayList<>();
            funcTreeVOS.forEach(funcTreeVO -> {
                if (StringUtils.isEmpty(funcTreeVO.sysKey)) {
                    return;
                }
                //拿到所有和当前系统有关的角色和权限
                List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.getROlePermissionBySysKey(Long.valueOf(funcTreeVO.sysKey));
                if (CollectionUtils.isEmpty(rolePermissionDOS)) {
                    return;
                }
                rolePermissionDOS.forEach(rolePermissionDO -> {
                    Boolean result;
                   RolePermissionDO updatePermission =new RolePermissionDO();
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
                    result= updateMenuValue(systemRootVO, funcTreeVO.updateNode);
                    //将得到的结果再转为json
                    String selectedContext = StringUtility.toJSONString(systemRootVO);
                    if (StringUtils.isEmpty(selectedContext)) {
                        logger.error("systemRootVO　转 json 失败,失败的roleId为[%s]", rolePermissionDO.getRoleId());
                    }
                    if (result ==false){
                        return;
                    }
                    //组装需要更新的数据
                    if (rolePermissionDO.getRoleId() != null) {
                        updatePermission.setSelectedContext(selectedContext);
                        updatePermission.setRoleId(rolePermissionDO.getRoleId());
                        updatePermission.setSysKey(rolePermissionDO.getSysKey());
                        updatePermissions.add(rolePermissionDO);
                    }
                });
            });
            updateRolePermissionAndRoleUser(updatePermissions);
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
    private Boolean updateMenuValue(SystemRootVO systemRootVO, List<NodeVO> lstNode) {
        if (systemRootVO == null || CollectionUtils.isEmpty(lstNode) || CollectionUtils.isEmpty(systemRootVO.menu)) {
            return false;
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
        return  true;
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
