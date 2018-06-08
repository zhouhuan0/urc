package com.yks.urc.mapper;

import com.yks.urc.entity.UserRoleDO;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * 用户-角色关系Mapper操作类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/8 14:47
 * @see IUserRoleMapper
 * @since JDK1.8
 */
public interface IUserRoleMapper {

    /**
     * Description:批量删除用户-角色关系
     *
     * @param : ids
     * @return:
     * @auther: lvcr
     * @date: 2018/6/8 15:06
     * @see
     */
    Integer deleteBatch(List<Integer> ids);


}