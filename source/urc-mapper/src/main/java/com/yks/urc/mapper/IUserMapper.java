package com.yks.urc.mapper;

import com.yks.urc.entity.UserDO;

import java.util.List;

public interface IUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    List<UserDO> listUsersByRoleId(Integer roleId);
}