package com.yks.urc.vo;

import java.io.Serializable;

public class RequestVO<T> implements Serializable {
    public String operator;
    public String requestId;

    public T data;

}