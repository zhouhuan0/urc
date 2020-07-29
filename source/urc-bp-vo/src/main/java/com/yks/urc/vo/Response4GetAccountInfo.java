package com.yks.urc.vo;

import java.util.List;

/**
 * @ProjectName: actmgr
 * @Package: com.yks.actmgr.third
 * @ClassName: Request4GetAccountInfo
 * @Author: zengzheng
 * @Description: getAccountInfo返回体
 * @Date: 2020/7/13 12:26
 * @Version: 1.0
 */
public class Response4GetAccountInfo {

    private List<AccountInfo4Third> list;

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


    public List<AccountInfo4Third> getList() {
        return list;
    }

    public void setList(List<AccountInfo4Third> list) {
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
