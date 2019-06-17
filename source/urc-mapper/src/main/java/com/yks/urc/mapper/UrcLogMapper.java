package com.yks.urc.mapper;

import java.util.List;

import com.yks.urc.entity.UrcLog;
import com.yks.urc.vo.LogListReqVo;

public interface UrcLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcLog record);

    int insertSelective(UrcLog record);

    UrcLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcLog record);

    int updateByPrimaryKeyWithBLOBs(UrcLog record);

    int updateByPrimaryKey(UrcLog record);
    
    /**
     * @Description: 根据条件查询
     * @author: zengzheng
     * @param conditionsMap
     * @return
     * @version: 2019年6月4日 上午9:43:42
     */
    List<UrcLog> selectUrcLogByConditions(LogListReqVo logListReqVo);

}