package com.yks.urc.service.api;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.SystemRootVO;

import java.util.List;

public interface IRoleService {

    List<RoleVO> getRolesByInfo(RoleVO roleVO);

    Integer addOrUpdateRoleInfo(String userName, RoleVO roleVO);

    RoleDO getRoleByRoleId(Integer roleId);

    List<UserInfoDO> getUserByRoleId(String roleId);

    void deleteRoles(List<Integer> lstRoleId);

    List<SystemRootVO> getUserAuthorizablePermission(String userName);

    List<RoleDO> getRolePermission(List<String> lstRoleId);

    void updateRolePermission(List<String> lstRoleId);

    ResultVO getRoleUser(List<String> lstRoleId);

    void updateUsersOfRole(List<RoleDO> lstRole);

    void copyRole(String strUserName,String newRoleName,String sourceRoleId);

    /**
     * 检查角色名是否重复
     * @param newRoleName
     * @param roleId
     * @return 返回值为0--表示不重复, 1--表示重复
     * @Author oujie
     * @Date 2018/6/12 16:57
     */
    ResultVO checkDuplicateRoleName(String operator, String newRoleName, String roleId);
}
