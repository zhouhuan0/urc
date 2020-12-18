package com.yks.urc.mapper;

import com.yks.urc.entity.PermissionDO;
import com.yks.urc.entity.PermitItemPosition;
import org.apache.ibatis.annotations.Param;

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

    void deleteBypositionIdAndKey(@Param("positionId") long positionId,@Param("roleSysKey") List<String> roleSysKey);

    List<String> getPermission(List<String> list);

    List<String> findOneSystemKey(@Param("sysType") Integer sysType);
}