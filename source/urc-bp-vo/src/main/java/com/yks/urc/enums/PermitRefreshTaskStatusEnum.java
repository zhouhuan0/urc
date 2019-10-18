package com.yks.urc.enums;

public enum PermitRefreshTaskStatusEnum {
    /**************************公共异常*****************************/
    WAIT_TO_DO("1", "待处理"),
    DOING("2", "处理中"),
    SUCCESS("3", "处理成功"),
    FAILED("4", "失败"),
    ;

    private String code;
    private String msg;

    PermitRefreshTaskStatusEnum(String code, String msg) {
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
