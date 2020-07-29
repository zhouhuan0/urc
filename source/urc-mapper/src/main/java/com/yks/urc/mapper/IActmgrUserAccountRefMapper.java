package com.yks.urc.mapper;

import com.yks.urc.entity.ActmgrUserAccountRefVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IActmgrUserAccountRefMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActmgrUserAccountRefVO record);

    int insertSelective(ActmgrUserAccountRefVO record);

    ActmgrUserAccountRefVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActmgrUserAccountRefVO record);

    int updateByPrimaryKeyWithBLOBs(ActmgrUserAccountRefVO record);

    int updateByPrimaryKey(ActmgrUserAccountRefVO record);

    int insertOrUpdate(@Param("lst") List<ActmgrUserAccountRefVO> lstRef);

    String getByUserNameAndEntityCode(@Param("userName") String userName, @Param("entityCode") String e_platformShopSite);
}