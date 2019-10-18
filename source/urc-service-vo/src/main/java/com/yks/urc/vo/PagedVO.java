package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

public class PagedVO<T> implements Serializable {
    public Long total;
    public List<T> list;

}