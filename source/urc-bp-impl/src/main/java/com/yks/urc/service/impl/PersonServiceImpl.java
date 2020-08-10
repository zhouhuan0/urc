
package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.dingding.client.vo.DingDeptVO;
import com.yks.urc.dingding.client.vo.DingUserVO;
import com.yks.urc.entity.Organization;
import com.yks.urc.entity.Person;
import com.yks.urc.entity.PersonOrg;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.fw.BeanProvider;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.lock.bp.api.ILockBp;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.OrganizationMapper;
import com.yks.urc.mapper.PersonMapper;
import com.yks.urc.mapper.PersonOrgMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserInfoVO;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public ResultVO getUserByDingOrgId(String dingOrgId, String pageNumber, String pageData) {
        PersonVO person = new PersonVO();
        person.setDingOrgId(dingOrgId);
        Query query = new Query(person, pageNumber, pageData);
        List<PersonVO> personList = personMapper.getUserByDingOrgId(query);
        long count = personMapper.getUserByDingOrgIdCount(query);
        PageResultVO pageResultVO = new PageResultVO(personList, count, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }


    @Override
    public ResultVO getUserByUserInfo(PersonVO person, String pageNumber, String pageData) {
        Query query = new Query(person, pageNumber, pageData);
        List<PersonVO> personList = personMapper.list(query);
        long count = personMapper.count(query);
        PageResultVO pageResultVO = new PageResultVO(personList, count, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    @Autowired
    private ILockBp lockBp;

    private String syncDingDingLockName = "SynPersonOrgFromDing";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveDingDingInfo(Map<String, List> initInfo) throws Exception {
        if (CollectionUtils.isEmpty(initInfo)) {
            return;
        }
        //删除部门表org，删除人员表person,删除，关系表
        organizationMapper.deleteAllOrg();
        personMapper.deleteAllPerson();
        personOrgMapper.deleteAllPersonOrg();

        //初始化人员表person,org,personOrg
        List<Organization> orgList = initInfo.get("org");
        List<Person> personList = initInfo.get("person");
        List<PersonOrg> personOrgList = initInfo.get("personOrg");
        if (orgList != null && orgList.size() > 0) {
            //插入部门表
            organizationMapper.insertBatchOrg(orgList);
        }
        if (personList != null && personList.size() > 0) {
            //dingUserId + phoneNum作为维度去重Person
            Set<Person> setData = new HashSet<Person>();
            setData.addAll(personList);
            personList.clear();
            personList = new ArrayList<>(setData);
            //插入人员表
            personMapper.insertBatchPerson(personList);
        }

        if (personOrgList != null && personOrgList.size() > 0) {
            //插入部门人员表
            personOrgMapper.insertBatchPersonOrg(personOrgList);
        }

        operationBp.addLog(this.getClass().getName(), "同步钉钉数据成功..", null);
    }

    @Autowired
    private ISessionBp sessionBp;

    public ResultVO pullAndSaveDingDingInfo() throws Exception {
        if (!lockBp.tryLock(syncDingDingLockName)) {
            return VoHelper.getSuccessResult("未获取到锁");
        }

        try {
            // 获取组织架构
            List<DingDeptVO> dingAllDept = dingApiProxy.getDingAllDept();
            // 递归获取组织架构下的人员
            Map<String, List> initInfo = initInfoValues(dingAllDept, sessionBp.getOperator());
            // 入库
            BeanProvider.getBean(IPersonService.class).saveDingDingInfo(initInfo);
            return VoHelper.getSuccessResult("同步完成");
        } catch (Exception ex) {
            logger.error("pullAndSaveDingDingInfo", ex);
            throw ex;
        } finally {
            lockBp.unlock(syncDingDingLockName);
        }
    }

    @Override
    public ResultVO SynPersonOrgFromDing(String userName) {
        fixedThreadPool.submit(new Runnable() {
            @Transactional(rollbackFor = Exception.class)
            @Override
            public void run() {
                try {
                    pullAndSaveDingDingInfo();
                } catch (Exception e) {
                    logger.error("同步钉钉数据出错", e);
                    operationBp.addLog(this.getClass().getName(), "同步钉钉数据出错..", e);
                }
            }
        });
        return VoHelper.getSuccessResult("触发成功，请等待");
    }


    private Map<String, List> initInfoValues(List<DingDeptVO> dingAllDept, String userName) throws Exception {
        Map<String, List> mapInfo = new HashMap<String, List>();
        List<Organization> initOrg = new ArrayList<Organization>();
        List<Person> initPerson = new ArrayList<Person>();
        List<PersonOrg> initPersonOrg = new ArrayList<PersonOrg>();
        if (dingAllDept != null && dingAllDept.size() > 0) {
            for (int i = 0; i < dingAllDept.size(); i++) {
                DingDeptVO dept = dingAllDept.get(i);
                Organization org = new Organization();
                JSONArray array = dingApiProxy.getDingParentDepts(String.valueOf(dept.id));
                org.setCreateBy(userName);
                org.setCreateTime(new Date());
                org.setDingOrgId(String.valueOf(dept.id));
                List<String> listFullIdPath = Arrays.asList(array.toJSONString().replace("[", "").replace("]", "").split(","));
                Collections.reverse(listFullIdPath);
                org.setFullIdPath(StringUtils.join(listFullIdPath.toArray(), "/"));
                JSONArray fullNamePath = getfullNamePath(dingAllDept, listFullIdPath);
                org.setFullNamePath(fullNamePath.toJSONString().replace("[", "").replace("]", "").replace("\"", "").replace(",", "/"));
                org.setModifiedBy(userName);
                org.setModifiedTime(new Date());
                org.setOrgLevel(array.size());
                org.setOrgName(dept.name);
                org.setParentDingOrgId(String.valueOf(dept.parentid));
                initOrg.add(org);

                //根据部门查询人员信息
                List<DingUserVO> userList = dingApiProxy.getDingMemberByDepId(String.valueOf(dept.id));
                if (userList != null && userList.size() > 0) {
                    for (int j = 0; j < userList.size(); j++) {
                        //初始化人员信息
                        DingUserVO user = userList.get(j);
                        Person person = new Person();
                        person.setBirthday(user.birthday);
                        person.setCreateBy(userName);
                        person.setCreateTime(new Date());
                        person.setDingId(user.unionid);
                        person.setDingUnionid(user.unionid);
                        person.setDingUserId(user.userid);
                        person.setEmail(user.email);
                        if (!StringUtil.isEmpty(user.gender)) {
                            if (user.gender.equals("男")) {
                                person.setGender(1);
                            } else if (user.gender.equals("女")) {
                                person.setGender(0);
                            } else {
                                person.setGender(2);
                            }
                        }
                        person.setJobNumber(user.jobnumber);
                        person.setJoinDate(new Date(user.hiredDate));
                        person.setLeaveDate(null);
                        person.setModifiedBy(userName);
                        person.setModifiedTime(new Date());
                        person.setPersonName(user.name);
                        person.setPersonNameCollage(PinyinHelper.convertToPinyinString(user.name, "", PinyinFormat.WITHOUT_TONE));
                        person.setPhoneNum(user.mobile);
                        person.setPosition(user.position);
                        initPerson.add(person);

                        //初始化人员、部门信息
                        PersonOrg personOrg = new PersonOrg();
                        personOrg.setCreateBy(userName);
                        personOrg.setCreateTime(new Date());
                        personOrg.setDingOrgId(String.valueOf(dept.id));
                        personOrg.setDingUserId(user.userid);
                        personOrg.setModifiedBy(userName);
                        personOrg.setModifiedTime(new Date());
                        initPersonOrg.add(personOrg);
                    }
                }
            }
        }
        mapInfo.put("org", initOrg);//部门集合
        mapInfo.put("person", initPerson);//人员集合
        mapInfo.put("personOrg", initPersonOrg);//人员部门关联集合
        return mapInfo;

    }


    private JSONArray getfullNamePath(List<DingDeptVO> dingAllDept, List<String> array) {
        JSONArray arrayName = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            long dingId = Long.parseLong(array.get(i));
            for (int j = 0; j < dingAllDept.size(); j++) {
                DingDeptVO dep = dingAllDept.get(j);
                if (dingId == dep.id) {
                    arrayName.add(i, dep.name);
                }
            }
        }
        return arrayName;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO fuzzSearchPersonByName(String operator, String userName) {

        try {
            List<UserInfoVO> infoVOList = userMapper.fuzzSearchUserByName(userName);
            if (CollectionUtils.isEmpty(infoVOList)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "查找的用户不存在");
            } else {
                for (UserInfoVO userInfoVO : infoVOList) {
                    if (!StringUtility.isNullOrEmpty(userInfoVO.dingUserId)) {
                        UserInfoVO userOrgVO = personMapper.getPersonOrgById(userInfoVO.dingUserId);
                        if (userOrgVO == null) {
                            continue;
                        } else {
                            userInfoVO.orgName = userOrgVO.orgName;
                            userInfoVO.parentOrgName = userOrgVO.parentOrgName;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return VoHelper.getSuccessResult(infoVOList);
        } catch (Exception e) {
            logger.error("未知错误", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO fuzzSearchPersonByName4Account(String operator, String userName, Integer exact, Integer pageData, Integer pageNum) {
        try {
            Integer start = 0;
            if (pageNum != null && pageNum > 0) {
                start = (pageNum - 1) * pageData;
            }
            List<UserInfoVO> infoVOList = userMapper.fuzzSearchUserByName4Account(userName, exact, start, pageData);
            logger.info(String.format("fuzzSearchPersonByName4Account operator:%s,userName:%s,exact:%s,start:%s,pageData:%s  |   infoVOList:%s",userName,exact,start,pageData, JSON.toJSONString(infoVOList)));
            return VoHelper.getSuccessResult(infoVOList);
        } catch (Exception e) {
            logger.error("未知错误", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    public ResultVO getDepartment(String orgLevel) {
        try {
            if (StringUtil.isEmpty(orgLevel)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
            }
            List<UserInfoVO> infoVOList = userMapper.getDepartmentByOrgLevel(Integer.valueOf(orgLevel));

            return VoHelper.getSuccessResult(infoVOList);
        } catch (Exception e) {
            logger.error("未知错误", e);
            return VoHelper.getErrorResult();
        }
    }
}
