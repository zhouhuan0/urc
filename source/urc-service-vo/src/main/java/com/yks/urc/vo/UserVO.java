/**
 * 〈一句话功能简述〉<br>
 * 〈用户信息〉
 *
 * @author 31076
 * @create 2018/6/5
 * @since 1.0.0
 */
package com.yks.urc.vo;

import java.io.Serializable;
import java.util.Date;

public class UserVO implements Serializable {
    private static final long serialVersionUID = -7653119695391553598L;
    /**
     * 用户Id
     */
    public String userId;
    /**
     * 用户名称
     */
    public String userName;
    /**
     * 是否存活
     */
    public boolean isActive;
    /**
     *
     */
    public String name;
    /**
     * 性别
     */
    public String gender;
    /**
     *   电话号码
     */
    public String phoneNum;
    /**
     *
     */
    public String dingUserId;
    /**
     *
     */
    public String dingId;
    /**
     *  生日
     */
    public Date birthday;
    /**
     *  加入集团时间
     */
    public Date joinDate;
    /**
     *
     */
    public Date leaveDate;
    /**
     *
     */
    public String jobNumber;
    /**
     *  邮箱
     */
    public String email;
    /**
     *  职位
     */
    public String position;
    /**
     * 密码
     */
    public String pwd;
    /**
     *
     */
    public String ip;
    /**
     * 拥有哪些系统功能权限
     */
    public String sysKey;
    /**
     *  票据
     */
    public String ticket;

}
