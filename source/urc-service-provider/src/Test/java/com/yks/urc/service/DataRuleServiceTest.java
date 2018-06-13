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
    public void assignDataRuleTempl2User(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator","admin");
        jsonObject.put("templId",1);
        List<String>  lstUserName = new ArrayList<>();
        lstUserName.add("admin21");
        lstUserName.add("admin22");
        lstUserName.add("admin23");
        lstUserName.add("admin24");
        jsonObject.put("lstUserName",lstUserName);
        ResultVO resultVO = dataRuleService.assignDataRuleTempl2User(jsonObject.toString());
        System.out.println(resultVO);
    }


    @Test
    public void getDataRuleTempl(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator","admin");
        jsonObject.put("pageNumber",1);
        jsonObject.put("pageData",4);
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("数据权限模板1").append(System.getProperty("line.separator"))
                .append("数据权限模板1").append(System.getProperty("line.separator"))
                .append("数据权限模板2").append(System.getProperty("line.separator"))
                .append("数据权限模板3").append(System.getProperty("line.separator"))
                .append("数据权限模板4").append(System.getProperty("line.separator"))
                .append("数据权限模板5").append(System.getProperty("line.separator"))
                .append("数据权限模板6").append(System.getProperty("line.separator"))
                .append("数据权限模板7").append(System.getProperty("line.separator"))
                .append("数据权限模板8").append(System.getProperty("line.separator"))
                .append("数据权限模板9").append(System.getProperty("line.separator"))
                .append("数据权限模板10");
        dataRuleTemplVO.templName=stringBuilder.toString();
        jsonObject.put("templ",dataRuleTemplVO);
        ResultVO<PageResultVO> resultVO = dataRuleService.getDataRuleTempl(jsonObject.toString());
        System.out.println(resultVO);
    }


}
