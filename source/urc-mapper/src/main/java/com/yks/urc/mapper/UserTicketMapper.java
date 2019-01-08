package com.yks.urc.mapper;

import com.yks.urc.entity.UserTicketDO;
import java.util.Map;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/8 16:13
 * 修改理由：
 * 修改内容：
 */
public interface UserTicketMapper {
    /**
     * 根据用户名查询用户的ticket信息
     * @param userName
     * @return
     */
    UserTicketDO selectUserTicketByUserName(String userName);
    /**
     * 用户ticket信息
     * @param map
     */
    void insertUserTicket(Map map);

    /**
     * 更新用户ticket表
     * @param map
     */
    void updateUserTicket(Map map);
}
