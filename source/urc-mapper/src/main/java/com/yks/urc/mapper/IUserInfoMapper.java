package com.yks.urc.mapper;

import com.yks.urc.entity.UserInfoDO;
import org.springframework.stereotype.Repository;

/**
 * 〈user_info操作mapper类〉
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/5/28 8:40
 * @see IUserInfoMapper
 * @since JDK1.8
 */
@Repository
public interface IUserInfoMapper {

    Integer insertUser(UserInfoDO userInfoPO);


}
