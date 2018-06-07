/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/7
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.vo.UserVO;

public interface IUserMapper {
    /**
     * 获取域名
     * @param userName
     * @return
     */
    UserVO getUserByName(String userName);
}
