package com.yks.urc.vo;

import java.util.List;
import java.util.Map;

public class SkuCategoryVO {

    //private List<BaseSkuInfoVO> firstCategory;
    private List<CategoryVO> firstCategory;
    private Map<String,String> availableStock;
    private Map<String,String> chineseName;
    private Map<String,String> costPrice;


    public Map<String, String> getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Map<String, String> availableStock) {
        this.availableStock = availableStock;
    }

    public Map<String, String> getChineseName() {
        return chineseName;
    }

    public void setChineseName(Map<String, String> chineseName) {
        this.chineseName = chineseName;
    }

    public Map<String, String> getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Map<String, String> costPrice) {
        this.costPrice = costPrice;
    }

  /*  public List<BaseSkuInfoVO> getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(List<BaseSkuInfoVO> firstCategory) {
        this.firstCategory = firstCategory;
    }*/

    public List<CategoryVO> getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(List<CategoryVO> firstCategory) {
        this.firstCategory = firstCategory;
    }
}
