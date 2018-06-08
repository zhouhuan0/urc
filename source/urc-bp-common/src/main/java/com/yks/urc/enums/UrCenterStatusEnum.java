package com.yks.urc.enums;

/**
 * 〈一句话功能简述〉
 * 用户权限系统返回码定义
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/8 17:14
 * @see UrCenterStatusEnum
 * @since JDK1.8
 */
public enum UrCenterStatusEnum {

    SUCCESS("000001", "success", "成功"),


    FAIL("000000", "fail", "失败");

    /**
     * 返回码
     */
    private String code;
    /**
     * 返回码定义
     */
    private String name;
    /**
     * 返回码描叙
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

     UrCenterStatusEnum(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
}
