package com.yks.urc.mapper;

import com.yks.urc.entity.PositionGroupVO;

import java.util.List;
import java.util.Map;

/**
 * 权限组操作Mapper类
 */
public interface IPositionGroupMapper {


    List<PositionGroupVO> getPermissionGroupByUser(Map<String, Object> queryMap);

    int getPermissionGroupByUserCount(Map<String, Object> queryMap);

    int deletePermissionGroup(String groupId);
}
