package com.yks.urc.enums;

public enum RoleLogEnum {
    FenPei_QuanXian("FenPei_QuanXian", "分配权限"),
    FenPei_YongHu("FenPei_YongHua", "分配用户");

    private String code;
    private String msg;

    RoleLogEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
