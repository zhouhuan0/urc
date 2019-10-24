package com.yks.urc.excel;

import java.util.List;

public class FileUploadRespVO {
	private String state;
    private String msg;

    private List<FileDataVO> data;

    public List<FileDataVO> getData() {
        return data;
    }

    public void setData(List<FileDataVO> data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
