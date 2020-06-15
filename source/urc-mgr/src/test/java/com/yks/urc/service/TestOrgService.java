package com.yks.urc.service;

import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.vo.PersonVO;

import com.yks.urc.vo.ResultVO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

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
	@Autowired
	IUrcMgr urcMgr;

	private Map map = new HashMap();
	private String operator = "zengzheng";
	private ResultVO resultVO;
	@Test
	public void testgetAllFuncPermit() throws Exception {
		String jsonStr = "{\"data\":{\"sysKeys\":[]},\"operator\":\"zengzheng\"}";
		map.put("userName", "zengzheng");
		map.put("operator", operator);
		String json = StringUtility.toJSONString(map);
		MotanSession.initialSession(json);
		resultVO = urcMgr.getUserInfoDetailByUserName(json);
		map.put("orgLevel", "2");
		map.put("operator", operator);
		json = StringUtility.toJSONString(map);
		MotanSession.initialSession(json);
		resultVO = urcMgr.getDepartment(json);
		System.out.println(StringUtility.toJSONString(resultVO));

	}


	@Test
    public void getAlldeptJson() throws Exception{
/*    	PersonVO personVO=new PersonVO();
    	personVO.setPersonName("aaa");
    	System.out.println(personService.getUserByUserInfo(personVO));*/
    	PersonVO person=new PersonVO();
//    	person.setOrgId("11111");
    	//person.setPhoneNum("17771054080");
    	personService.getUserByDingOrgId("11111", "0", "10");
    }
    
}
