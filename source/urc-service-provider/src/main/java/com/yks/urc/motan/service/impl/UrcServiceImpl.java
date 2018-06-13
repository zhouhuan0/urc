package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.Person;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.*;
import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class UrcServiceImpl implements IUrcService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPersonService personService;


    @Autowired
    private IOrganizationService organizationService;

    @Autowired
    private IDataRuleService dataRuleService;

    @Override
    public String syncUserInfo(UserVO curUser) {
        return StringUtility.toJSONString_NoException(userService.syncUserInfo(curUser));
    }

    @Override
    public String login(Map<String, String> map) {
        UserVO authUser = new UserVO();
        authUser.userName = map.get("userName");
        authUser.pwd = map.get("pwd");
        authUser.ip = map.get("ip");
        // UserVO curUser, UserVO authUser
        return StringUtility.toJSONString_NoException(userService.login(authUser));
    }

    @Override
    public String syncDingOrgAndUser() {
        return StringUtility.toJSONString_NoException(personService.SynPersonOrgFromDing("hand"));
    }

    @Override
    public String getUserByDingOrgId(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("templ").toString(), PersonVO.class);
        return StringUtility.toJSONString_NoException(personService.getUserByDingOrgId(personVo, pageNumber, pageData));
    }

    @Override
    public String getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("templ").toString(), PersonVO.class);
        return StringUtility.toJSONString_NoException(personService.getUserByUserInfo(personVo, pageNumber, pageData));
    }

    @Override
    public String getAllOrgTree() {
        return StringUtility.toJSONString_NoException(organizationService.getAllOrgTree());
    }

    /**
     * Description: 快速分配数据权限模板给用户
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 13:53
     * @see
     */
    @Override
    public String assignDataRuleTempl2User(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.assignDataRuleTempl2User(jsonStr));
    }

    /**
     * Description: 根据templId获取数据权限模板
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:55
     * @see
     */
    @Override
    public String getDataRuleTemplByTemplId(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.getDataRuleTemplByTemplId(jsonStr));
    }

    /**
     * Description: 获取数据权限模板
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:56
     * @see
     */
    @Override
    public String getDataRuleTempl(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.getDataRuleTempl(jsonStr));
    }


}
