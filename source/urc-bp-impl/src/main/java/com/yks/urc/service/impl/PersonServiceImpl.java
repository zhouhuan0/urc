package com.yks.urc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.StringUtil;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.dingding.client.vo.DingDeptVO;
import com.yks.urc.dingding.client.vo.DingUserVO;
import com.yks.urc.entity.Organization;
import com.yks.urc.entity.Person;
import com.yks.urc.entity.PersonOrg;
import com.yks.urc.entity.UserDO;
import com.yks.urc.lock.DistributedReentrantLock;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.OrganizationMapper;
import com.yks.urc.mapper.PersonMapper;
import com.yks.urc.mapper.PersonOrgMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.TaskVO;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;

@Service
public class PersonServiceImpl implements IPersonService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DingApiProxy dingApiProxy;
	
	@Autowired
	private PersonMapper personMapper;
	
	@Autowired
	private PersonOrgMapper personOrgMapper;
	
	@Autowired
	private OrganizationMapper organizationMapper;
	
    @Autowired
    private IUserMapper userMapper;
    
    @Autowired
    private IOperationBp operationBp;

	@Override
	public ResultVO getUserByDingOrgId(String dingOrgId,int pageNumber, int pageData) {
		PersonVO person=new PersonVO();
		person.setDingOrgId(dingOrgId);
		Query query=new Query(person, pageNumber, pageData);
		List<PersonVO> personList= personMapper.getUserByDingOrgId(query);
		long count= personMapper.getUserByDingOrgIdCount(query);
		PageResultVO pageResultVO = new PageResultVO(personList,count,pageData);
		return VoHelper.getSuccessResult(pageResultVO);
	}


	@Override
	public ResultVO getUserByUserInfo(PersonVO person,int pageNumber, int pageData) {
		Query query=new Query(person, pageNumber, pageData);
		List<PersonVO> personList= personMapper.list(query);
		long count= personMapper.count(query);
		PageResultVO pageResultVO = new PageResultVO(personList,count,pageData);
		return VoHelper.getSuccessResult(pageResultVO);
	}
	
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
	DistributedReentrantLock lock = new DistributedReentrantLock("SynPersonOrgFromDing");
	@Transactional(rollbackFor = Exception.class)
	public ResultVO SynPersonOrgFromDing(String userName) {
		if(lock.tryLock()){
			logger.info("开始同步钉钉数据");
			//得到钉钉所有的部门
			try {
			  //先准备初始化参数	
					fixedThreadPool.submit(new Runnable() {
					@Transactional(rollbackFor = Exception.class)
		            public void run() {
		            	List<DingDeptVO> dingAllDept;
						try {
							dingAllDept = dingApiProxy.getDingAllDept();
							Map<String,List> initInfo=initInfoValues(dingAllDept,userName);
			    			
							//删除部门表org，删除人员表person,删除，关系表
							organizationMapper.deleteAllOrg();
							personMapper.deleteAllPerson();
							personOrgMapper.deleteAllPersonOrg();
							
							//初始化人员表person,org,personOrg
							List<Organization> orgList=initInfo.get("org");
							List<Person> personList=initInfo.get("person");
							List<PersonOrg> personOrgList=initInfo.get("personOrg");
							
							//插入部门表
							organizationMapper.insertBatchOrg(orgList);
							//插入人员表
							personMapper.insertBatchPerson(personList);
							//插入部门人员表
							personOrgMapper.insertBatchPersonOrg(personOrgList);
							operationBp.addLog(this.getClass().getName(), "同步钉钉数据成功..", null);
						} catch (Exception e) {
							logger.error("同步钉钉数据出错，message={}",e.getMessage());
							operationBp.addLog(this.getClass().getName(), "同步钉钉数据出错..", e);
						}
		            }
		        });
					
				TaskVO taskVO=new TaskVO();	
				taskVO.taskId="1";
				return VoHelper.getResultVO(taskVO, "1", CommonMessageCodeEnum.SUCCESS.getDesc());
				
			} catch (Exception e) {
				logger.error("同步钉钉数据出错，message={}",e.getMessage());
				operationBp.addLog(this.getClass().getName(), "同步钉钉数据出错..", e);
				return VoHelper.getResultVO("0", "同步钉钉数据出错");
			}finally{
				lock.unlock();
				logger.info("同步钉钉数据完成");
			}
		}else{
			if("system".equals(userName)){
				//手动触发正在执行..记录日志
				operationBp.addLog(this.getClass().getName(), "手动触发正在执行..", null);
			}else{
				//定时任务触发正在执行..记录日志
				operationBp.addLog(this.getClass().getName(), "定时任务正在执行..", null);
			}
			TaskVO taskVO=new TaskVO();	
			taskVO.taskId="1";
			return VoHelper.getResultVO(taskVO, "1", CommonMessageCodeEnum.SUCCESS.getDesc());
		}
		
	}
	


	private Map<String,List> initInfoValues(List<DingDeptVO> dingAllDept, String userName) throws Exception{
		 Map<String,List> mapInfo=new HashMap<String, List>();
		List<Organization> initOrg=new ArrayList<Organization>();
		List<Person> initPerson=new ArrayList<Person>();
		List<PersonOrg> initPersonOrg=new ArrayList<PersonOrg>();
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
				DingUserVO user=userList.get(j);
				Person person=new Person();
				person.setBirthday(user.birthday);
				person.setCreateBy(userName);
				person.setCreateTime(new Date());
				person.setDingId(user.unionid);
				person.setDingUnionid(user.unionid);
				person.setDingUserId(user.userid);
				person.setEmail(user.email);
				if(!StringUtil.isEmpty(user.gender)){
					if(user.gender.equals("男")){
						person.setGender(1);
					}else if (user.gender.equals("女")){
						person.setGender(0);
					}else{
						person.setGender(2);
					}
				}
				person.setJobNumber(user.jobnumber);
				person.setJoinDate(new Date(user.hiredDate));
				person.setLeaveDate(null);
				person.setModifiedBy(userName);
				person.setModifiedTime(new Date());
				person.setPersonName(user.name);
				person.setPhoneNum(user.mobile);
				person.setPosition(user.position);
				initPerson.add(person);
				
				//初始化人员、部门信息
				PersonOrg personOrg=new PersonOrg();
				personOrg.setCreateBy(userName);
				personOrg.setCreateTime(new Date());
				personOrg.setDingOrgId(String.valueOf(dept.id));
				personOrg.setDingUserId(user.userid);
				personOrg.setModifiedBy(userName);
				personOrg.setModifiedTime(new Date());
				initPersonOrg.add(personOrg);
			}
		}
		mapInfo.put("org", initOrg);//部门集合
		mapInfo.put("person", initPerson);//人员集合
		mapInfo.put("personOrg", initPersonOrg);//人员部门关联集合
		return mapInfo;
		
	}


	private JSONArray getfullNamePath(List<DingDeptVO> dingAllDept, JSONArray array) {
		JSONArray arrayName=new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			long dingId=array.getLongValue(i);
			for (int j = 0; j < dingAllDept.size(); j++) {
				DingDeptVO dep=dingAllDept.get(j);
				if(dingId==dep.id){
					arrayName.add(i, dep.name);
				}
			}
		}
		return arrayName;
	}
	

	
}
