package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
//    List<RoleDO> listRoleByPage(RoleDO roleDO,int currPage, int pageSize);

    RoleDO getRoleByRoleId(Integer roleId);

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

}
