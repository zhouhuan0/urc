package com.yks.urc.service.api;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.SystemRootVO;

import java.util.List;

public interface IRoleService {

    List<RoleVO> getRolesByInfo(RoleVO roleVO);

    Integer addOrUpdateRoleInfo(RoleVO roleVO);

    List<UserInfoDO> getUserByRoleId(String roleId);

    void deleteRoles(List<String> lstRoleId);

    List<SystemRootVO> getUserAuthorizablePermission(String userName);

    List<RoleDO> getRolePermission(List<String> lstRoleId);

    void updateRolePermission(List<String> lstRoleId);

    List<RoleVO> getRoleUser(List<String> lstRoleId);

    void updateUsersOfRole(List<RoleDO> lstRole);

    void copyRole(String strUserName,String newRoleName,String sourceRoleId);

}
