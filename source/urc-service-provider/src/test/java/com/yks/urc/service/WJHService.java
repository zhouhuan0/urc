package com.yks.urc.service;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.lang.DRL5Expressions.literal_return;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.helper.VoHelper;


public class WJHService extends BaseServiceTest {
	
	private static Logger LOG = LoggerFactory.getLogger(WJHService.class);
	
	@Autowired
	private IOrganizationService orgService;
	
	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IPersonService personService;
	
	@Autowired
	private IUserService userService;
	
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IDataRuleService dataRuleService;
	
	
	@Autowired
	private DingApiProxy dingApiProxy;

    @Test
    public void getAlldeptJson() throws Exception{
/*    	PersonVO personVO=new PersonVO();
    	personVO.setPersonName("aaa");
    	System.out.println(personService.getUserByUserInfo(personVO));*/
    	PersonVO person=new PersonVO();
//    	person.setOrgId("11111");
    	//person.setPhoneNum("17771054080");
    	personService.getUserByDingOrgId("1", "0", "10");
    }
    
    

    @Test
    public void SynPersonOrgFromDing() throws Exception{
    	//personService.SynPersonOrgFromDing("hand");
    	personService.getUserByDingOrgId("1", "0", "");
    }
    
    
    @Test
    public void getDataRuleByUser() throws Exception{
    	List<String> userName=new ArrayList<>();
    	userName.add("panyun");
    	ResultVO  dataVO=dataRuleService.getDataRuleByUser(userName,"panyun");
    	System.out.println(StringUtility.toJSONString(dataVO));
    }
    
    
    
    @Test
    public void getRoleUser() throws Exception{
    	
    	String jsonStr= "{\"lstRoleId\":[\"1529649147479000001\"],\"operator\":\"linwanxain\"}";
    			 
    			 
    	JSONObject jsonObject = StringUtility.parseString(jsonStr);
         String operator = jsonObject.getString("operator");
         
         
         if (StringUtility.isNullOrEmpty(operator)) {
         }
         if (StringUtility.isNullOrEmpty(jsonObject.getString("lstRoleId"))) {
         }
         List<String> roleList = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);
         
    	ResultVO dataVO=roleService.getRoleUser(operator, roleList);
    	
    	System.out.println(StringUtility.toJSONString(dataVO));

    }
    
    
    @Test
    public void getUserAuthorizablePermission() throws Exception{
    	
    	System.out.println(StringUtility.toJSONString(permissionService.getUserAuthorizablePermission("panyun")));

    }
    
    @Test
    public void fuzzySearchUsersByUserName() throws Exception{
    	
    	userService.fuzzySearchUsersByUserName(null, null, null, "");
    	//System.out.println(StringUtility.toJSONString(permissionService.getUserAuthorizablePermission("panyun")));
    }
    
    
    @Test
    public void dataRuleService() throws Exception{
    	List<String> lstUserName=new ArrayList<>();
    	lstUserName.add("潘韵");
    	lstUserName.add("程立夫");
    	
    	
    	System.out.println(StringUtility.toJSONString(dataRuleService.getDataRuleByUser(lstUserName,"sdfd")));
    	
    }
    
    
    @Test
    public void getRolePermission() throws Exception{
    	List<String> lstRoleId=new ArrayList<>();
    	lstRoleId.add("1529746874242000036");
    	
    	System.out.println(StringUtility.toJSONString(roleService.getRolePermission("linwanxian", lstRoleId)));
    	
    }
    
    
    @Test
    public void getRoleByRoleId() throws Exception{

    	String jsonStr="{\"roleId\":\"1530015877269000015\",\"operator\":\"tangyong\"}";
    	System.out.println(StringUtility.toJSONString(roleService.getRoleByRoleId(jsonStr)));
    	
    }
    
    
    
    @Test
    public void updateUsersOfRole() throws Exception{
    	String jsonStr="{\"lstRole\":[{\"roleId\":\"162964914747900000000\",\"lstUserName\":[\"wjh3\",\"wjh2\"]},{\"roleId\":\"1629649147479000002\",\"lstUserName\":[\"wjh4\",\"wjh5\"]}],\"operator\":\"wujianghui\"}";
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        List<RoleVO> lstRole = StringUtility.jsonToList(jsonObject.getString("lstRole"), RoleVO.class);
        List<String> userList=lstRole.get(0).getLstUserName();
        for (int i = 0; i < userList.size(); i++) {
			System.out.println(userList.get(i));
		}
    	System.out.println(StringUtility.toJSONString(roleService.updateUsersOfRole(lstRole, operator)));
    	
    }
    
    
    
}
