package com.yks.urc.enums;

/* 
 * 文件名：MessageCodeEnum.java
 * 版权：Copyright by www.youkeshu.com 
 * 描述：状态码公共定义
 * 创建人：sandy 
 * 创建时间：2018/3/9   
 * 修改理由： 
 * 修改内容： 
 */
public enum CommonMessageCodeEnum {


    SUCCESS("000001", "success", "成功"),


    FAIL("000000", "fail", "失败"),


    PARAM_NULL("000002", "parameterError", "请求参数为空"),


    PARAM_INVALID("000003", "parameterInvalid", "请求参数非法"),


    JSON_ERROR("000004", "jsonError", "JSON转换失败"),


    DB_ERROR("000005", "dbError", "数据库异常"),


    NETWORK_ERROR("000006", "networkError", "网络异常"),


    UNKOWN_ERROR("000007", "unkownError", "未知异常"),

    HANDLE_DATA_EXCEPTION("000008","handleDataException","数据处理异常");

    private String code;
    /***
     * 状态码定义
     */
    private String name;
    /***
     * 状态码详细描述
     */
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    CommonMessageCodeEnum(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }


}
