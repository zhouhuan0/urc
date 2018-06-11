package com.yks.urc.service;

import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.service.api.IPersonService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceProviderApplication.class)*/
public class TestOrgService extends BaseServiceTest {
	
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
