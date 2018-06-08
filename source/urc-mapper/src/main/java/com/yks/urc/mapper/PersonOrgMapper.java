package com.yks.urc.mapper;

import com.yks.urc.entity.PersonOrg;

public interface PersonOrgMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PersonOrg record);

    int insertSelective(PersonOrg record);

    PersonOrg selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PersonOrg record);

    int updateByPrimaryKey(PersonOrg record);
}