package com.yks.urc.cache.bp.impl;

import com.yks.urc.cache.bp.api.IUpdateAffectedUserPermitCache;
import com.yks.urc.entity.*;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.mapper.*;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.ISessionBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/19 15:19
 * 修改理由：
 * 修改内容：
 */
@Component
public class UpdateAffectedUserPermitCacheImpl implements IUpdateAffectedUserPermitCache {
    private Logger logger = LoggerFactory.getLogger(UpdateAffectedUserPermitCacheImpl.class);
    private static final Integer BATCH_INSERT_LIMIT = 2000;

    @Autowired
    private IUserAffectedMapper userAffectedMapper;
    @Autowired
    private ISessionBp sessionBp;
    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    PermissionMapper permitMapper;
    @Autowired
    IPermitStatBp permitStatBp;
    @Autowired
    private IRolePermissionMapper rolePermitMapper;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    /**
     * 删除角色时 将角色id和角色下的用户保存到urc_role_user_affected表
     *
     * @param userNames 用户域账号
     */
    @Override
    public void saveAffectedUser(List<String> userNames) {
        // 更新角色下的用户的权限缓存
        List<String> userNameList = userNames.stream().distinct().collect(Collectors.toList());
        permitStatBp.updateUserPermitCache(userNameList);
    }


    /**
     * @param roleId 角色id
     */
    @Override
    public void assignAllPermit2SuperAdministrator(Long roleId) {
        RoleDO roleFromDb = roleMapper.getRoleByRoleId(String.valueOf(roleId));
        if (roleFromDb == null){ throw new URCBizException(String.format("roleId:%s不存在", roleId), ErrorCode.E_000000);}
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
    }
}
