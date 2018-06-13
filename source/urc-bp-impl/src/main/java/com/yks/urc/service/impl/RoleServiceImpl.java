package com.yks.urc.service.impl;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import com.yks.urc.entity.UserInfoDO;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IRoleService;

import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    
    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private ISeqBp seqBp;

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
     * 1、roleName存在则更新
     * 2、roleName不存在则新增
     * 3、
     *
     * @param :userName
     * @param roleVO
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 14:23
     * @see
     */
    @Transactional
    @Override
    public Integer addOrUpdateRoleInfo(String userName, RoleVO roleVO) {
        /*需要判断当前用户是否管理员、普通业务员只能编辑自己创建的角色
          业务员是否需要再次对权限范围校验*/
//        if (!roleDO.isAuthorizable()) {
//            RoleDO roleDO1 = roleMapper.ge
//        }
        /*1、新增或更新角色基础信息*/
        RoleDO roleDO = new RoleDO();
        BeanUtils.copyProperties(roleVO, roleDO);
        int rtn = roleMapper.insertOrUpdate(roleDO);
         /*2、新增或更新 角色-功能权限 信息*/
           /*1、获取功能权限信息*/
        List<PermissionVO> permissionVOS = roleVO.getSelectedContext();
          /*2、功能权限数据为空，说明没有赋权操作，故不需要添加或更新角色-功能权限 信息*/
        if (permissionVOS == null || permissionVOS.isEmpty()) {
            //permissionVOS没有数据时候，说明没有更改角色-权限数据
        }else{
            insertOrUpdateRolePermission(userName, permissionVOS, roleDO, rtn);
        }
        /*2、根据角色Id删除用户角色关系，再新增*/
        /*3、根据角色Id删除角色权限关系，再新增*/
        return null;
    }

    private void insertOrUpdateRolePermission(String userName, List<PermissionVO> permissionVOS, RoleDO roleDO, Integer rtn) {

            if (rtn == 1) {
            /*2.1 rtn=1，表示是新增角色操作*/
                List<RolePermissionDO> rolePermissionDOS = new ArrayList<>();
                for (PermissionVO permissionVO : permissionVOS) {
                    RolePermissionDO rolePermissionDO = new RolePermissionDO();
                    rolePermissionDO.setSelectedContext(permissionVO.getSysContext());
                    rolePermissionDO.setSysKey(permissionVO.getSysKey());
                    rolePermissionDO.setRoleId(roleDO.getId());
                    rolePermissionDO.setCreateBy(userName);
                    rolePermissionDO.setCreateTime(new Date());
                    rolePermissionDO.setModifiedBy(userName);
                    rolePermissionDO.setModifiedTime(new Date());
                    rolePermissionDOS.add(rolePermissionDO);
                }
               /*批量添加 角色-操作权限映射关系*/
                rolePermissionMapper.insertBatch(rolePermissionDOS);
            } else if (rtn > 1) {
              /*2.2 如果rtn>1，表示是更新角色操作；*/
                List<RolePermissionDO> rolePermissionDOS = new ArrayList<>();
                for (PermissionVO permissionVO : permissionVOS) {
                    RolePermissionDO rolePermissionDO = new RolePermissionDO();
                    rolePermissionDO.setSelectedContext(permissionVO.getSysContext());
                    rolePermissionDO.setSysKey(permissionVO.getSysKey());
                    rolePermissionDO.setRoleId(roleDO.getId());
                    rolePermissionDO.setCreateBy(userName);
                    rolePermissionDO.setCreateTime(new Date());
                    rolePermissionDO.setModifiedBy(userName);
                    rolePermissionDO.setModifiedTime(new Date());
                    rolePermissionDOS.add(rolePermissionDO);
                }
                /*批量更新或添加 角色-操作权限映射关系*/
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
    public RoleDO getRoleByRoleId(String roleId) {
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
    public ResultVO getRoleUser(List<String> lstRoleId) {
        /*非管理员只能查看自己创建的角色*/
    	List<RoleVO> roleList=new ArrayList<>();
    	for (int i = 0; i < lstRoleId.size(); i++) {
    		RoleDO roleDO=roleMapper.getRoleByRoleId(Long.parseLong(lstRoleId.get(i)));
    		RoleVO roleVO=new RoleVO();
    		roleVO.setRoleName(roleDO.getRoleName());
    		roleVO.setRoleId(roleDO.getRoleId());
    		if(roleDO.isAuthorizable()){
    			//管理员
    			List<String> lstUserName= userMapper.listAllUsersUserName();
    			roleVO.setLstUserName(lstUserName);
    		}else{
    			//非管理员
    			List<String> lstUserName =userMapper.listUsersUserNameByRoleId(roleDO.getRoleId());
    			roleVO.setLstUserName(lstUserName);
    		}
    		roleList.add(roleVO);
		}
        /*查询用户角色关系表*/
        return VoHelper.getSuccessResult(roleList);
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
     *  复制角色将创建一个新的角色，并将原角色的权限自动授予给新角色
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/6 15:04
     * @see
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyRole(String operator, String newRoleName, String sourceRoleId) {
        /*非admin用户只能管理自己创建的角色*/
        RoleDO roleDO = getRoleInfo(operator, newRoleName, sourceRoleId);
        //复制角色信息
        roleDO.setRoleId(seqBp.getNextRoleId());
        roleDO.setRoleName(newRoleName);
        roleMapper.insert(roleDO);
        /*复制对应的角色权功能限关系*/
        List<RoleDO> rolePermission = getRolePermission(Arrays.asList(sourceRoleId));
//        rolePermission.stream().forEach(role -> {
//            role.getPermissionDO()
//        });
    }

    /**
     *
     * 判断当前操作者权限,再获取被复制角色信息
     * 1.管理员用户可以复制所有角色信息
     * 2.普通用户只能复制自己创建的角色信息
     */
    private RoleDO getRoleInfo(String operator, String newRoleName, String sourceRoleId){
        if (roleMapper.checkDuplicateRoleName(newRoleName, null)){
            throw new RuntimeException("角色名已存在");
        }
        //判断当前被复制角色是否为当前用户创建的角色
        RoleDO roleDO = roleMapper.getRoleByRoleId(sourceRoleId);
        //判断当前用户是否为管理员用户
        if (roleMapper.isAdminAccount(operator) || operator.equals(roleDO.getCreateBy())){
            return roleDO;
        }
        throw new RuntimeException("普通用户只能复制自己创建的角色信息");
    }

    @Override
    public ResultVO<Integer> checkDuplicateRoleName(String operator, String newRoleName, String roleId) {
        return VoHelper.getSuccessResult(roleMapper.checkDuplicateRoleName(newRoleName, roleId)?1:0);
    }
}
