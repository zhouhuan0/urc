package com.yks.urc.service.impl;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.service.api.IRoleService;

import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
public class RoleServiceImpl implements IRoleService {

    private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private IRoleMapper roleMapper;

    @Autowired
    private IRolePermissionMapper rolePermissionMapper;

    @Autowired
    private IUserRoleMapper userRoleMapper;

    /**
     * Description:
     * 1、根据多个条件获取角色列表
     * 2、admin可以查看所有角色；业务人员只能查看自己创建的角色
     *
     * @param : roleVO
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:09
     * @see
     */
    @Override
    public List<RoleVO> getRolesByInfo(RoleVO roleVO) {
        /*1、判断当前用户是否是管理员*/
        /*2、1如果是管理员，则根据条件查询所有角色*/
        /*2、2如果非管理员，则只能查询当前用户创建的角色*/
        return null;
    }

    /**
     * Description: 新增或更新角色基础信息、功能权限、用户
     * 1、角色Id存在则更新
     * 2、角色Id不存在则新增
     *
     * @param :
     * @param roleVO
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:23
     * @see
     */
    @Override
    public Integer addOrUpdateRoleInfo(RoleVO roleVO) {
        /*需要判断是否管理员、普通业务员只能编辑自己创建的角色
          业务员是否需要再次对权限范围校验*/
        /*1、新增或更新角色基础信息*/
        /*2、根据角色Id删除用户角色关系，再新增*/
        /*3、根据角色Id删除角色权限关系，再新增*/
        return null;
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
    public RoleDO getRoleByRoleId(Integer roleId) {
        RoleDO roleDO = roleMapper.getRoleByRoleId(roleId);
        return roleDO;
    }

    /**
     * Description: 获取角色关联的用户
     * 1、…
     * 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:35
     * @see
     */
    @Override
    public List<UserInfoDO> getUserByRoleId(String roleId) {
        /*1、非admin角色的用户需要判断该角色是否是自己创建的角色*/
        /*2、根据roleId查询用户*/
        return null;
    }

    /**
     * Description:
     * 批量删除角色 包括角色-功能权限关系   用户-角色关系  角色
     *
     * @param : lstRoleId
     * @auther: lvcr
     * @date: 2018/6/6 14:44
     * @see
     */
    @Transactional
    @Override
    public void deleteRoles(List<Integer> lstRoleId) {
        /*1、非管理员用户只能管理自己创建的角色*/
        /*2、先删除用户角色关系，再删角色权限关系数据，然后删角色信息*/

        /** 1、 删除角色权限关系*/
        rolePermissionMapper.deleteBatch(lstRoleId);
        /*2、删除用户角色关系*/
        userRoleMapper.deleteBatch(lstRoleId);
        /*3、删除角色信息*/
        roleMapper.deleteBatch(lstRoleId);

    }

    /**
     * Description:
     * 1、分配权限--获取指定(当前)用户可授权给其它角色的功能权限
     * 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:51
     * @see
     */
    @Override
    public List<SystemRootVO> getUserAuthorizablePermission(String userName) {
        /*获取当前用户可授权给其它角色的功能权限*/
        return null;
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
    public List<RoleDO> getRolePermission(List<String> lstRoleId) {
        /*需要判断角色是否是当前用户创建的*/
        return null;
    }

    /**
     * Description:
     * 1、分配权限--同时更新多个角色的功能权限
     * 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:57
     * @see
     */
    @Override
    public void updateRolePermission(List<String> lstRoleId) {
        /*非管理员只能修改自己关联的角色*/

    }

    /**
     * Description:
     * 1、分配权限--获取多个角色已有的用户
     * 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:01
     * @see
     */
    @Override
    public List<RoleVO> getRoleUser(List<String> lstRoleId) {
        /*非管理员只能查看自己创建的角色*/
        /*查询用户角色关系表*/
        return null;
    }

    /**
     * Description:
     * 1、分配权限--同时更新多个角色的用户
     * 2、…
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:02
     * @see
     */
    @Override
    public void updateUsersOfRole(List<RoleDO> lstRole) {
        /*非管理员只能查看自己创建的角色*/
        /*根据角色删除用户角色关系表*/
        /*新增用户角色关系表*/
    }

    /**
     * Description:
     * 1、复制角色
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:04
     * @see
     */
    @Override
    public void copyRole(String strUserName, String newRoleName, String sourceRoleId) {
        /*非admin用户只能管理自己创建的角色*/
        /*复制对应的角色权功能限关系*/
    }
}
