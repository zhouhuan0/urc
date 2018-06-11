package com.yks.urc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yks.urc.ServiceProviderApplication;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.entity.Organization;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.vo.OrgVO;
import com.yks.urc.vo.PersonVO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceProviderApplication.class)
public class TestOrgService {
	
	private static Logger LOG = LoggerFactory.getLogger(TestOrgService.class);
	
	@Autowired
	private IOrganizationService orgService;
	
	@Autowired
	private IPersonService personService;
	
	
	@Autowired
	private DingApiProxy dingApiProxy;

    @Test
    public void getAlldeptJson() throws Exception{
/*    	PersonVO personVO=new PersonVO();
    	personVO.setPersonName("aaa");
    	System.out.println(personService.getUserByUserInfo(personVO));*/
    	
    	System.out.println(	dingApiProxy.getDingParentDepts("64271064"));
    }
    
}
