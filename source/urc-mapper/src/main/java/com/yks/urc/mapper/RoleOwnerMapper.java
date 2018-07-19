/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author lwx
 * @create 2018/7/19
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.entity.RoleOwnerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleOwnerMapper {
    /**
     * 通过roleId 查找owner表
     * @param:
     * @return
     * @Author lwx
     * @Date 2018/7/19 10:14
     */
    List<RoleOwnerDO> selectOwnerByRoleId(@Param("roleId") long roleId);
    /**
     * 通过owner 查询
     * @param:
     * @return
     * @Author lwx
     * @Date 2018/7/19 15:23
     */
    List<RoleOwnerDO> selectOwnerByOwner(@Param("owner") String owner);
}
