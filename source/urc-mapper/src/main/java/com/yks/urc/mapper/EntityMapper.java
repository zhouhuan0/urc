package com.yks.urc.mapper;

import com.yks.urc.entity.Entity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Entity record);

    int insertSelective(Entity record);

    Entity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Entity record);

    int updateByPrimaryKey(Entity record);
    /**
     * 通过enctity_code　查询实体 和授权方式一对一
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/15 9:39
     */
    Entity selectEntityByCode(@Param("entityCode") String entityCode);
}