package com.yks.urc.mapper;

import com.yks.urc.entity.UrcSystemAdministrator;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrcSystemAdministratorMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcSystemAdministrator record);

    int insertSelective(UrcSystemAdministrator record);

    UrcSystemAdministrator selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcSystemAdministrator record);

    int updateByPrimaryKey(UrcSystemAdministrator record);

    int deleteBySysKey(String sysKey);

    int insertBatch(@Param("list") List<UrcSystemAdministrator> list);

    List<UrcSystemAdministrator> selectBySysKey(@Param("list") List<String> sysKeys);

    //根据管理员类型查询管理员拥有权限的启用状态系统
    List<String> selectSysKeyByAdministratorType(@Param("userName") String userName,@Param("type")Integer type);
}