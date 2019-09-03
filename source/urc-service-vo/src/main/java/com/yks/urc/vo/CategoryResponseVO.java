package com.yks.urc.vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class CategoryResponseVO implements Serializable {
    private static final long serialVersionUID = 3003166758646712288L;
    
    @JSONField(name = "children")
    private List<CategoryResponseVO> subCategorys;
    private String cateId;
    private String cateNameCn;


    public List<CategoryResponseVO> getSubCategorys() {
		return subCategorys;
	}

	public void setSubCategorys(List<CategoryResponseVO> subCategorys) {
		this.subCategorys = subCategorys;
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
