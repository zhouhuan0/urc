package com.yks.urc.service;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataRuleServiceTest extends BaseServiceTest {

    @Autowired
    private IDataRuleService dataRuleService;


    @Test
    public void getDataRuleTemplByTemplId() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator","admin");
        jsonObject.put("templId",1);
        ResultVO<DataRuleTemplVO> resultVO = dataRuleService.getDataRuleTemplByTemplId(jsonObject.toString());
        System.out.println(resultVO);
    }


    @Test
    public void getDataRuleTempl(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator","admin");
        jsonObject.put("pageNumber",1);
        jsonObject.put("pageData",3);
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        dataRuleTemplVO.templName="数据权限模板1";
        jsonObject.put("templ",dataRuleTemplVO);
        ResultVO<PageResultVO> resultVO = dataRuleService.getDataRuleTempl(jsonObject.toString());
        System.out.println(resultVO);
    }

}
