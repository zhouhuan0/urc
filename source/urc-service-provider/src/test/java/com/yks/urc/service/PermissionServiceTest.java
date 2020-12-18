package com.yks.urc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PermissionServiceTest extends BaseServiceTest {

    @Autowired
    private IPermissionService permissionService;


    @Test
    public void getUserPermissionStatList() {
        JSONObject jsonObject = new JSONObject();
        Integer pageNumber = 1;
        Integer pageData = 10;
        jsonObject.put("operator", "dcadmin");
        jsonObject.put("pageNumber", pageNumber);
        jsonObject.put("pageData", pageData);
        ResultVO resultVO = permissionService.getUserPermissionList(jsonObject.toString());
        System.out.println(resultVO);
    }


    @Autowired
    private ISerializeBp serializeBp;



    @Autowired
    private IRoleService roleService;

    @Test
    public void getRolePermission() throws Exception{
        List<String> lstRoleId=new ArrayList<>();
        lstRoleId.add("1572485640340000418");
        System.out.println(StringUtility.toJSONString(roleService.getRolePermission("panyun", lstRoleId)));
        System.out.println(StringUtility.toJSONString(roleService.getRolePermission("hehuake", lstRoleId)));

    }

}
