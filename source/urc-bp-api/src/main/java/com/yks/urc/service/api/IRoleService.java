package com.yks.urc.service.api;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.UserVO;

import java.util.List;

public interface IRoleService {

    ResultVO<PageResultVO> getRolesByInfo(String jsonStr);

    ResultVO addOrUpdateRoleInfo(String jsonStr);

    ResultVO<RoleVO> getRoleByRoleId(String jsonStr);

    ResultVO getUserByRoleId(String operator,String roleId);

    void deleteRoles(List<Integer> lstRoleId);

    List<SystemRootVO> getUserAuthorizablePermission(String userName);

    ResultVO getRolePermission(List<String> lstRoleId);

    void updateRolePermission(List<String> lstRoleId);

    ResultVO getRoleUser(String operator, List<String> lstRoleId);

    ResultVO updateUsersOfRole(List<RoleVO> lstRole,String operator);

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

	/**
	 * 角色过期处理，由taskScheduler调用
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午11:32:09
	 */
	void handleExpiredRole();
}
