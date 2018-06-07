package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import org.springframework.stereotype.Repository;

/**
 * 角色操作Mapper类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/6 11:17
 * @see IRoleMapper
 * @since JDK1.8
 */
@Repository
public interface IRoleMapper {

    Integer insert(RoleDO roleDO);
}
