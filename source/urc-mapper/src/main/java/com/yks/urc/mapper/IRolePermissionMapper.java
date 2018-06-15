package com.yks.urc.mapper;

import com.yks.urc.entity.RolePermissionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * 角色-功能权限Mapper操作类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/8 15:06
 * @see IRolePermissionMapper
 * @since JDK1.8
 */
public interface IRolePermissionMapper {

    /**
     * Description: 批量删除角色-操作权限关系
     *
     * @param : ids
     * @return:
     * @auther: lvcr
     * @date: 2018/6/8 15:08
     * @see
     */
    Integer deleteBatch(List<Integer> ids);


    /**
     * Description: 批量操作角色-操作权限关系 以roleId+sysKey作为唯一约束
     * 1、记录存在则更新
     * 2、记录不存在则新增
     *
     * @param : rolePermissionDOS
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/9 12:14
     * @see
     */
    Integer insertAndUpdateBatch(List<RolePermissionDO> rolePermissionDOS);

    /**
     * Description: 批量新增角色-操作权限关系
     *
     * @param : rolePermissionDOS
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/9 16:09
     * @see
     */
    Integer insertBatch(List<RolePermissionDO> rolePermissionDOS);

    /**
     * 获取指定用户可授权给其它角色的功能权限
     * @param userName
     * @return
     */
	List<RolePermissionDO> getUserAuthorizablePermission(String userName);

	/**
	 * 获取多个角色已有的功能权限
	 * @param roleId
	 * @return
	 */
	List<RolePermissionDO> getRolePermission(String roleId);

    /**
     *  通过用户名获取角色对应的sys_key
     * @param  userName
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/14 15:20
     */
    List<String> getSysKetByRoleAndUserName(@Param("userName") String userName);
}