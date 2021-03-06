package com.yks.urc.mapper;

import com.yks.urc.entity.PositionGroupVO;
import com.yks.urc.vo.PermissionVO;
import com.yks.urc.vo.PositionPower;
import com.yks.urc.vo.UserByPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 权限组操作Mapper类
 */
public interface IPositionGroupMapper {


    List<PositionGroupVO> getPermissionGroupByUser(Map<String, Object> queryMap);

    int getPermissionGroupByUserCount(Map<String, Object> queryMap);

    int deletePermissionGroup(String groupId);

    boolean existSuperAdmin(@Param("positionIds") List<Long> positionIds);

    String getPermissionGroupName(String groupId);

    List<UserByPosition> getPositions(String groupId);

    List<PermissionVO> getSelectedContext(@Param("groupId") String groupId,@Param("sysType") Integer sysType);

    List<UserByPosition> getPositionList(@Param("positionName") String positionName);

    List<PermissionVO> getPositionPermission(String positionId);

    List<UserByPosition> getPositionInfoByPermitKey(Map<String, Object> queryMap);

    int getPositionInfoByPermitKeyCount(Map<String, Object> queryMap);

    List<PositionPower> positionPowerList(@Param("positionIds") List<String> positionIds);

}
