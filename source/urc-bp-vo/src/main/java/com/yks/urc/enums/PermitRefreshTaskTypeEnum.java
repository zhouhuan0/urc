package com.yks.urc.enums;

public enum PermitRefreshTaskTypeEnum {
    /**************************公共异常*****************************/
    REFRESH_USER("1", "刷新permit_item_user表"),
    REFRESH_SYS("2", "刷新permit_item_info表"),
    ;
    private String code;
    private String msg;

    PermitRefreshTaskTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public byte getCodeByte() {
        return Byte.parseByte(code);
    }

    public String getMsg() {
        return msg;
    }
}
