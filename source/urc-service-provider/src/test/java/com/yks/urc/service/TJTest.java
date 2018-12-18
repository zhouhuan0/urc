package com.yks.urc.service;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TJTest extends BaseServiceTest {
    @Autowired
    IUrcService iUrcService;
    @Test
    public void getAllOrgTree_test(){
        System.out.println(StringUtility.toJSONString( iUrcService.getAllOrgTree()));
    }
    @Test
    public void getAllOrgTreeAndUser_test(){
        System.out.println(StringUtility.toJSONString(iUrcService.getAllOrgTreeAndUser()));

    }
}
