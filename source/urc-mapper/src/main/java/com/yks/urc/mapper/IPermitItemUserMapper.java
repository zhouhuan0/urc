package com.yks.urc.mapper;

import com.yks.urc.entity.PermitItemUserVO;

public interface IPermitItemUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermitItemUserVO record);

    int insertSelective(PermitItemUserVO record);

    PermitItemUserVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermitItemUserVO record);

    int updateByPrimaryKey(PermitItemUserVO record);
}