package com.yks.urc.mapper;

import com.yks.urc.entity.Permission;
import org.apache.ibatis.annotations.Param;

public interface PermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKeyWithBLOBs(Permission record);

    int updateByPrimaryKey(Permission record);

    /**
     * 获取systemKey
     *
     * @param sysKey
     * @return Permission
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 9:28
     */
    Permission getSystemKey(@Param("sysName") String sysKey);
    
    Permission getPermissionBySysKey(@Param("sysKey") String sysKey);
}