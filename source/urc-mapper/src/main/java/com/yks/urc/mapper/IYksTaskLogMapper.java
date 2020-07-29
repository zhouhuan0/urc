package com.yks.urc.mapper;


import com.yks.urc.entity.YksTaskLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IYksTaskLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YksTaskLogVO record);

    int insertSelective(YksTaskLogVO record);

    YksTaskLogVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YksTaskLogVO record);

    int updateByPrimaryKeyWithBLOBs(YksTaskLogVO record);

    int updateByPrimaryKey(YksTaskLogVO record);

    List<YksTaskLogVO> selectByLogger(@Param("logger") String logger);
}