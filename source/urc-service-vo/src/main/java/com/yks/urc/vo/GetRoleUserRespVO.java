/**
 * 〈一句话功能简述〉<br>
 * 〈角色基础信息〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GetRoleUserRespVO {

    /**
     * roleId
     */
    public String roleId;
    /**
     * 用户名 和域账号
     */
    public List<UserRespVO> lstUser;
}
