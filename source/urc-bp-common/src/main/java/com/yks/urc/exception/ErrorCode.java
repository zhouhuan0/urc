/*
 * 文件名：ErrorCode.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：OuJie
 * 创建时间：2018年06月19日
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.exception;

/**
 * @author OuJie
 * @version 1.2
 * @date 2018年06月19日
 * @see ErrorCode
 * @since JDK1.8
 */
public enum  ErrorCode {
    /**************************公共异常*****************************/
    E_000000("000000","失败"),E_000001("000001","成功"),E_000002("000002","请求参数为空"),
    E_000003("000003","请求参数非法"),E_000004("000004","JSON转换失败"),E_000005("000005","数据库异常"),
    E_000006("000006","网络异常"),E_000007("000007","未知异常"),E_000008("000008","数据处理异常"),
    /**************************账号异常*****************************/
    E_100001("100001","账号密码错误"),E_100002("100002","登录超时"),E_100003("100003","账号没有权限"),
    E_100004("100004","没有检测到登录用户信息"),E_100005("100005","用户账号正确"),E_100006("100006","用户功能权限版本正确"),
    E_100007("100007","用户功能权限版本错误"),
    /**************************角色异常*****************************/
    E_101001("101001","角色名已存在"),E_101002("101002","普通用户只能复制自己创建的角色信息"),;
    private String state;
    private String msg;

    ErrorCode(String state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }
}
