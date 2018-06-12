package com.yks.urc.mapper;

import com.yks.urc.entity.UserRoleDO;
import com.yks.urc.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * 用户-角色关系Mapper操作类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/8 14:47
 * @see IUserRoleMapper
 * @since JDK1.8
 */
public interface IUserRoleMapper {

    /**
     * Description:批量删除用户-角色关系
     *
     * @param : ids
     * @return:
     * @auther: lvcr
     * @date: 2018/6/8 15:06
     * @see
     */
    Integer deleteBatch(List<Integer> ids);

    /**
     * 根据用户名获取sysKey
     *
     * @param String
     * @return List<UserRoleDO>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 15:59
     */
    List<UserRoleDO> getSysKeyByUser(@Param("userName") String userName);
}