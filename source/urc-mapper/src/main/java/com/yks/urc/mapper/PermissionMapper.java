package com.yks.urc.mapper;

import com.yks.urc.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    Permission getSystemKey(@Param("sysKey") String sysKey);
    /**
     * 获取 systemname
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/14 15:56
     */
    //List<Permission> getSysKeyAndName( List<String> sysKey);

    /**
     * 通过sysKey 获取permission
     *
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/15 9:44
     */
   Permission getPermission(@Param("sysKey") String sysKey);
}