package com.yks.urc.mapper;

import com.yks.urc.entity.PermitItemInfoVO;
import com.yks.urc.vo.FunctionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IPermitItemInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermitItemInfoVO record);

    int insertSelective(PermitItemInfoVO record);

    PermitItemInfoVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermitItemInfoVO record);

    int updateByPrimaryKey(PermitItemInfoVO record);

    int addOrUpdate(@Param("lstAllKey") List<FunctionVO> lstAllKey, @Param("userName") String userName);
}