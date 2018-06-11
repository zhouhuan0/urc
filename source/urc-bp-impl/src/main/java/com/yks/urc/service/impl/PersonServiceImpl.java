package com.yks.urc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.dingding.client.vo.DingDeptVO;
import com.yks.urc.dingding.client.vo.DingUserVO;
import com.yks.urc.entity.Organization;
import com.yks.urc.entity.Person;
import com.yks.urc.mapper.PersonMapper;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;

@Service
public class PersonServiceImpl implements IPersonService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private DingApiProxy dingApiProxy;
	
	@Autowired
	private PersonMapper personMapper;

	@Override
	public ResultVO getUserByDingOrgId(String dingOrgId) {
		List<Person> personList= personMapper.getUserByDingOrgId(dingOrgId);
		if(personList!=null&&personList.size()>0){
			return VoHelper.getSuccessResult(personList);
		}else{
			return  VoHelper.getErrorResult();
		}
	}


	@Override
	public ResultVO getUserByUserInfo(PersonVO person) {
		List<PersonVO> personList= personMapper.getUserByUserInfo(person);
		if(personList!=null&&personList.size()>0){
			return VoHelper.getSuccessResult(personList);
		}else{
			return  VoHelper.getErrorResult();
		}
	}

	
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

	@Override
	public void SynUserFromUserInfo(String userName) {
/*		try {
			fixedThreadPool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					//得到钉钉所有的部门
					List<DingDeptVO> dingAllDept=dingApiProxy.getDingAllDept();
					
					//删除部门表org，删除人员表person,删除，关系表
					
					
					//添加操作
					
					return null;
				}
			
			
			});
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//得到钉钉所有的部门
		try {
			List<DingDeptVO> dingAllDept=dingApiProxy.getDingAllDept();
			
			
			
			
			//删除部门表org，删除人员表person,删除，关系表
			
			//添加操作
			//初始化部门表org参数
			//List<Organization> initOrg=initOrgValues(dingAllDept,userName);
			//初始化人员表person
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	


	private Map<String,List> initOrgValues(List<DingDeptVO> dingAllDept, String userName) throws Exception{
		
		 Map<String,List> mapInfo=new HashMap<String, List>();
		List<Organization> initOrg=new ArrayList<Organization>();
		List<Person> initPerson=new ArrayList<Person>();
		for (int i = 0; i < dingAllDept.size(); i++) {
			DingDeptVO dept=dingAllDept.get(i);
			
			
			Organization org=new Organization();
			JSONArray array=dingApiProxy.getDingParentDepts(String.valueOf(dept.id));
			org.setCreateBy(userName);
			org.setCreateTime(new Date());
			org.setDingOrgId(String.valueOf(dept.id));
			org.setFullIdPath(array.toJSONString().replace(",","/"));
			JSONArray fullNamePath=getfullNamePath(dingAllDept,array);
			org.setFullNamePath(fullNamePath.toJSONString().replace(",", "/"));
			org.setModifiedBy(userName);
			org.setModifiedTime(new Date());
			org.setOrgLevel(array.size());
			org.setOrgName(dept.name);
			org.setParentDingOrgId(String.valueOf(dept.parentid));
			initOrg.add(org);
			
			
			//根据部门查询人员信息
			
			List<DingUserVO> userList=dingApiProxy.getDingMemberByDepId(String.valueOf(dept.id));
			for (int j = 0; j < userList.size(); j++) {
				//初始化人员信息
/*				DingUserVO user=userList.get(j);
				Person person=new Person();
				person.setBirthday(user.birthday);
				person.setCreateBy(userName);
				person.setCreateTime(new Date());
				person.setDingId(dingId);
				person.setDingUnionid(user.unionid);
				person.setDingUserId(user.userid);
				person.setEmail(user.email);
				person.setGender(Integer.parseInt(user.gender));
				person.setJobNumber(user.jobnumber);
				person.setJoinDate(new Date(user.hiredDate));
				person.setLeaveDate(null);
				person.setModifiedBy(userName);
				person.setModifiedTime(new Date());
				person.setPersonName(user.name);
				person.setPhoneNum(user.mobile);
				person.setPosition(user.position);*/
				
			}
			
		}
		mapInfo.put("org", initOrg);//部门集合
		
		return null;
		
	}


	private JSONArray getfullNamePath(List<DingDeptVO> dingAllDept, JSONArray array) {
		JSONArray arrayName=new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			long dingId=array.getLongValue(i);
			for (int j = 0; j < dingAllDept.size(); j++) {
				DingDeptVO dep=dingAllDept.get(j);
				if(dingId==dep.id){
					arrayName.add(i, dep.name);;
				}
			}
		}
		return arrayName;
	}
	

	
}
