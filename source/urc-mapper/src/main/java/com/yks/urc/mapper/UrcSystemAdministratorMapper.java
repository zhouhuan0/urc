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
}