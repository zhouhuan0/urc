package com.yks.urc.vo;

import java.util.List;

/**
 * @Description
 * @Author zengzheng
 * @Date 2020/7/28 19:21
 */
public class Response4GetUserAccountInfo {

    private List<UserInfo4Third> list;

    /**
     * 总条数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 每页条数
     */
    private int pageSize;


    public List<UserInfo4Third> getList() {
        return list;
    }

    public void setList(List<UserInfo4Third> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
