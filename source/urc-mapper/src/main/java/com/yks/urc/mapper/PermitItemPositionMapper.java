package com.yks.urc.mapper;

import com.yks.urc.entity.PermitItemPosition;

import java.util.List;

public interface PermitItemPositionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermitItemPosition record);

    int insertSelective(PermitItemPosition record);

    PermitItemPosition selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermitItemPosition record);

    int updateByPrimaryKey(PermitItemPosition record);

    void deleteBypositionId(long positionId);

    void insertPosition(List<PermitItemPosition> list);
}