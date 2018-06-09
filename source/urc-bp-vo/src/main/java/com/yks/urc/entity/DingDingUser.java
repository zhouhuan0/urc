/**
 * 〈一句话功能简述〉<br>
 * 〈员工钉钉id、是否禁用信息〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/7
 * @since 1.0.0
 */
package com.yks.urc.entity;

import java.io.Serializable;

public class DingDingUser implements Serializable {
    private static final long serialVersionUID = 8572322957045776482L;
    /**
     * 钉钉用户名
     */
    public String username;
    /**
     * 钉钉用户id
     */
    public String ding_userid;
    /**
     * 表示是否启用， 66050表示禁用，其他的表示启用。
     */
    public String userAccountControl;
}
