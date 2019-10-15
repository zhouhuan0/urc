package com.yks.urc.mapper;

import com.yks.urc.entity.PermitItemUserVO;
import com.yks.urc.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IPermitItemUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermitItemUserVO record);

    int insertSelective(PermitItemUserVO record);

    PermitItemUserVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermitItemUserVO record);

    int updateByPrimaryKey(PermitItemUserVO record);

    List<Resp_getUserListByPermitKey> getUserListByPermitKey(RequestVO<Req_getUserListByPermitKey> req);

    Long getUserListByPermitKeyTotal(RequestVO<Req_getUserListByPermitKey> req);

    Integer deleteByUserName(@Param("userName") String userName);

    Integer addOrUpdatePermitItemUser(@Param("lstAllKey") List<FunctionVO> lstAllKey, @Param("userName") String userName);

}