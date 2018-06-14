package com.yks.urc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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


    @Test
    public void addOrUpdateDataRuleTempl(){
        JSONObject colObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        /*列权限1*/
        JSONObject calJsonObj1 = new JSONObject();
        calJsonObj1.put("entityCode","entityProduct");
           JSONObject colJson1 = new JSONObject();
           colJson1.put("fieldCode","name");
           colJson1.put("showType",0);
        calJsonObj1.put("colJson",colJson1);
            JSONObject colJson2 = new JSONObject();
            colJson2.put("fieldCode","price");
            colJson2.put("showType",0);
        calJsonObj1.put("colJson",colJson2);
        jsonArray.add(calJsonObj1);

        /*列权限2*/
        JSONObject calJsonObj2 = new JSONObject();
        calJsonObj2.put("entityCode","entitySupplier");
        JSONObject colJson21 = new JSONObject();
            colJson21.put("fieldCode","addr");
            colJson21.put("showType",0);
        calJsonObj2.put("colJson",colJson21);
        JSONObject colJson22 = new JSONObject();
            colJson22.put("fieldCode","city");
            colJson2.put("showType",0);
        calJsonObj2.put("colJson",colJson22);
        jsonArray.add(calJsonObj2);
        colObject.put("col",jsonArray);

        /*行权限*/
        JSONObject rowObject = new JSONObject();
        rowObject.put("isAnd",1);
        JSONArray subWhereClause = new JSONArray();
         JSONObject subWhereClause1 = new JSONObject();
         subWhereClause1.put("fieldCode","fieldPlatform");
         subWhereClause1.put("entityCode","platform");
         subWhereClause1.put("oper","in");
         subWhereClause1.put("operValues","[\"eBay\"，\"WISH\"]");
         subWhereClause.add(subWhereClause1);

        JSONObject subWhereClause2 = new JSONObject();
        subWhereClause2.put("fieldCode","fieldSite");
        subWhereClause2.put("oper","in");
        subWhereClause2.put("operValues","[\"美国\"，\"英国\"]");
        subWhereClause.add(subWhereClause2);

        JSONObject subWhereClause3 = new JSONObject();
        subWhereClause3.put("fieldCode","fieldProductSku");
        subWhereClause3.put("oper","in");
        subWhereClause3.put("operValues","[\"SKU001\"]");
        subWhereClause.add(subWhereClause3);

        rowObject.put("subWhereClause",subWhereClause);


        JSONObject listDataRuleSys = new JSONObject();
        listDataRuleSys.put("sysKey","001");
        listDataRuleSys.put("row",rowObject);
        listDataRuleSys.put("col",colObject);

        JSONObject dataRuleTemplVO = new JSONObject();
        dataRuleTemplVO.put("templName","采购一部3C产品组方案1");
        dataRuleTemplVO.put("remark","给xxxx的方案");
        dataRuleTemplVO.put("lstDataRuleSys",listDataRuleSys);
        ResultVO resultVO = dataRuleService.addOrUpdateDataRuleTempl(dataRuleTemplVO.toString());
        System.out.println(resultVO);
    }


}
