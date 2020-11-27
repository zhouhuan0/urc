package com.yks.urc.mapper;

import com.yks.urc.entity.UrcSystemAdministrator;

public interface UrcSystemAdministratorMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcSystemAdministrator record);

    int insertSelective(UrcSystemAdministrator record);

    UrcSystemAdministrator selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcSystemAdministrator record);

    int updateByPrimaryKey(UrcSystemAdministrator record);
}