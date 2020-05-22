package com.yks.urc.service;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.*;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


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
    private IUrcService urcService;
	
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IDataRuleService dataRuleService;

    @Autowired
    private ICsService csService;

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
    public void addCsUserGroup() throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("center_platform_id","2");
        jsonObject.put("platformName","222");
        jsonObject.put("groupId","31");
        jsonObject.put("groupName","3331");
        jsonObject.put("operator","3333");
        csService.addCsUserGroup(jsonObject.toJSONString());
    }


    @Test
    public void updateRolePermission() throws Exception{

    	
        List<RoleVO> lstRole = new ArrayList<>();
        RoleVO roleVO=new RoleVO();
        roleVO.setRoleId("1539160322094000002");
        lstRole.add(roleVO);
        roleService.updateRolePermission("wujianghui", lstRole);
    }
    
    

    @Test
    public void SynPersonOrgFromDing() throws Exception{
    	personService.SynPersonOrgFromDing("hand");
    	//personService.getUserByDingOrgId("1", "0", "");
    }

    @Test
    public void getRolesByInfo() throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNumber",1);
        jsonObject.put("pageData",20);
        jsonObject.put("operator","wujianghui");
        RoleVO role =new RoleVO();
        role.setRoleName("wjh");
        jsonObject.put("role",role);
        System.out.println(StringUtility.toJSONString( roleService.getRolesByInfo(jsonObject.toString())));
    }
    
    
    @Test
    public void getDataRuleByUser() throws Exception{
    	List<String> userName=new ArrayList<>();
    	userName.add("panyun");
    	ResultVO  dataVO=dataRuleService.getDataRuleByUser(userName,"panyun","001");
    	System.out.println(StringUtility.toJSONString(dataVO));
    }


    @Test
    public void roleIsSuperAdmin() throws Exception{
        ResultVO  dataVO= roleService.operIsSuperAdmin("panyun");
        System.out.println(StringUtility.toJSONString(dataVO));
    }
    
    
    
    @Test
    public void getRoleUser() throws Exception{
    	
    	String jsonStr= "{\"lstRoleId\":[\"1529649147479000001\",\"1529743116993000004\"],\"operator\":\"panyun\"}";
    			 
    			 
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
    	
    	System.out.println(StringUtility.toJSONString(permissionService.getUserAuthorizablePermission("yanxianbiao")));

    }

    @Test
    public void getPlatformShopByEntityCode() throws Exception{

        ResultVO resultVO=dataRuleService.getPlatformShopByEntityCode("wujianghui","E_PlatformShopSite");
        System.out.println(StringUtility.toJSONString(resultVO));
    }
    
    @Test
    public void fuzzySearchUsersByUserName() throws Exception{
    	
    	userService.fuzzySearchUsersByUserName(null, null, null, "");
    	//System.out.println(StringUtility.toJSONString(permissionService.getUserAuthorizablePermission("panyun")));
    }


    @Test
    public void dataRuleService() throws Exception{
    	List<String> lstUserName=new ArrayList<>();
    	lstUserName.add("wujianghui1");
    	lstUserName.add("wujianghui");
    	
    	
    	System.out.println(StringUtility.toJSONString(dataRuleService.getDataRuleByUser(lstUserName,"panyun","001")));
    	
    }
    
    
    @Test
    public void getRoleByRoleId() throws Exception{

    	String jsonStr="{\"roleId\":\"1530015877269000015\",\"operator\":\"tangyong\"}";
    	System.out.println(StringUtility.toJSONString(roleService.getRoleByRoleId(jsonStr)));
    	
    }
    
    
    
    @Test
    public void updateUsersOfRole() throws Exception{
    	//String jsonStr="{\"lstRole\":[{\"roleId\":\"1629649147479000002\",\"lstUserName\":[\"wjh3\",\"wjh2\"]},{\"roleId\":\"1629649147479000002\",\"lstUserName\":[\"wjh4\",\"wjh5\"]}],\"operator\":\"wujianghui\"}";

        String jsonStr="{\"lstRole\":[{\"roleId\":\"1548061463810000015\",\"lstUserName\":[\"songguanye\",\"tangjianbo\",\"linwanxian\"]}],\"ticket\":\"d6c7367691d79cca35a8711da935a947\",\"operator\":\"songguanye\",\"funcVersion\":\"15184e8647c6d5f9e68cbfc9b06bd98d\",\"moduleUrl\":\"/user/rolemanagement/allocUser/\",\"personName\":\"songguanye\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(jsonStr);
        urcService.updateUsersOfRole(jsonStr);



    }
    
    
    
}
