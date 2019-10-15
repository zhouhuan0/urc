package com.yks.urc.vo;

import java.io.Serializable;

public class ResultPagedVO<T> extends ResultVO {
    private static final long serialVersionUID = 2516742518077177675L;
    public String state;
    public String msg;
    public PagedVO<T> data;

}