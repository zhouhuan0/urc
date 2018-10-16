package com.yks.urc.mapper;

import com.yks.urc.entity.CsPlatformGroup;
import org.apache.ibatis.annotations.Param;

public interface CsPlatformGroupMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CsPlatformGroup record);

    int insertSelective(CsPlatformGroup record);

    CsPlatformGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CsPlatformGroup record);

    int updateByPrimaryKey(CsPlatformGroup record);

    CsPlatformGroup selectByPlantIdAndGroupId(@Param(value = "platformId") String centerPlatformId,@Param(value = "groupId") String groupId);

    CsPlatformGroup selectByGroupId(@Param(value = "groupId") String groupId);

    int deleteByGroupId(@Param(value = "groupId") String groupId);
}