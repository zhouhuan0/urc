package com.yks.urc.vo;

import java.util.List;

public class BaseSkuInfoVO {
    private List<BaseSkuInfoVO> category;
    private String cateId;
    private String cateNameCn;

    public List<BaseSkuInfoVO> getCategory() {
        return category;
    }

    public void setCategory(List<BaseSkuInfoVO> category) {
        this.category = category;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getCateNameCn() {
        return cateNameCn;
    }

    public void setCateNameCn(String cateNameCn) {
        this.cateNameCn = cateNameCn;
    }
}
