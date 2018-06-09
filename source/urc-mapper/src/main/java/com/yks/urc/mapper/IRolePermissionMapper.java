package com.yks.urc.mapper;

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
}