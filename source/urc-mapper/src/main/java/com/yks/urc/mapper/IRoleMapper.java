package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 角色操作Mapper类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/6 11:17
 * @see IRoleMapper
 * @since JDK1.8
 */
@Repository
public interface IRoleMapper {

    /**
     * Description: 添加角色
     *
     * @param :roleDO
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/7 18:28
     * @see
     */
    Integer insert(RoleDO roleDO);

    /**
     * Description: 根据roleName的唯一约束，roleName不重复则添加，roleName重复则更新
     * 1、返回1说明添加成功
     * 2、返回2说明更新成功
     *
     * @param : roleDO
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/7 18:26
     * @see
     */
    Integer insertOrUpdate(RoleDO roleDO);

    /**
     * Description: 批量删除角色
     *
     * @param : ids
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/7 18:39
     * @see
     */
    Integer deleteBatch(List<Integer> ids);

    /**
     * Description: 根据多个条件搜索角色，并分页显示
     *
     * @param : roleDO 角色对象作为条件
     *          currPage 页数
     *          pageSize  每页条数
     * @return: List<RoleDO>
     * @auther: lvcr
     * @date: 2018/6/7 18:42
     * @see
     */
    List<RoleDO> listRolesByPage(Map<String, Object> data);

    /**
     * Description:  根据roleId获取角色信息
     *
     * @param :roleId
     * @return:RoleDO
     * @auther: lvcr
     * @date: 2018/6/14 11:41
     * @see
     */
    RoleDO getRoleByRoleId(@Param("roleId") Long roleId);


    /**
     * 根据用户名 获取角色名称
     *
     * @param roleName
     * @return String
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/11 16:57
     */
    String selectRoleName(@Param("roleName") String roleName);

    List<String> getFuncJsonByUserAndSysKey(@Param("userName") String userName, @Param("sysKey") String sysKey);

    /**
     * 给定角色名的数据是否已存在
     *
     * @param newRoleName
     * @param roleId
     * @return 数量
     * @Author oujie
     * @Date 2018/6/12 16:57
     */
    boolean checkDuplicateRoleName(@Param("newRoleName") String newRoleName, @Param("roleId") String roleId);

    /**
     * 判断当前用户是否为超级管理员用户
     *
     * @param userName
     * @return true-是 false-否
     * @Author oujie
     * @Date 2018/6/13 17:27
     */
    boolean isAdminAccount(@Param("userName") String userName);

    /**
     * Description: 根据角色名获取角色
     *
     * @param :roleName
     * @return:RoleDO
     * @auther: lvcr
     * @date: 2018/6/14 8:45
     * @see
     */
    RoleDO getByRoleName(@Param("roleName") String roleName);

    /**
     * Description: 获取总数
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 11:26
     * @see
     */
    Long getCounts(@Param("createBy") String createBy);

	/**
	 * 获取所有过期的角色关联的用户
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午11:43:20
	 */
	List<String> getUsersOfAllExpiredRole();

	/**
	 * 将所有过期的角色 is_active 设置为0
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午11:43:39
	 */
	List<RoleDO> updateAllExpiredRole();
}
