package com.yks.urc.service.impl;

import java.sql.RowIdLifetime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.yks.urc.entity.PersonOrg;
import com.yks.urc.entity.UserAndPersonDO;
import com.yks.urc.entity.UserDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.PersonOrgMapper;
import com.yks.urc.vo.OrgTreeAndUserVO;
import com.yks.urc.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.entity.Organization;
import com.yks.urc.mapper.OrganizationMapper;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.vo.OrgVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.util.CollectionUtils;

@Service
public class OrganizationServiceImpl implements IOrganizationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DingApiProxy dingApiProxy;

    @Autowired
    private IRoleMapper roleMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private PersonOrgMapper personOrgMapper;
    @Autowired
    private IUserMapper iUserMapper;

    @Override
    public ResultVO getAllOrgTree() {
        JSONArray deptJosn = null;
        try {
            List<Organization> orgList = organizationMapper.queryAllDept();
            List<OrgVO> orgListVO = new ArrayList<OrgVO>();
            for (Organization org : orgList) {
                OrgVO orgVO = new OrgVO();
                orgVO.dingOrgId = org.getDingOrgId();
                orgVO.orgName = org.getOrgName();
                orgVO.parentDingOrgId = org.getParentDingOrgId();
                orgListVO.add(orgVO);
            }
            deptJosn = treeDingDeptList(orgListVO, 0);
        } catch (Exception e) {
            VoHelper.getErrorResult();
        }

        return VoHelper.getSuccessResult(deptJosn);
    }

    @Override
    public ResultVO getAllOrgTreeAndUser() {
        List<OrgTreeAndUserVO> deptJosn = null;
        try {
            List<Organization> orgList = organizationMapper.queryAllDept();
            List<OrgTreeAndUserVO> orgTreeAndUserVOList = new ArrayList<>();
            for (Organization org : orgList) {
                OrgTreeAndUserVO orgTreeAndUserVO = new OrgTreeAndUserVO();
                orgTreeAndUserVO.key = org.getDingOrgId();
                orgTreeAndUserVO.title = org.getOrgName();
                //??????????????????0??????????????????
                orgTreeAndUserVO.isUser = 0;
                orgTreeAndUserVO.parentDingOrgId = org.getParentDingOrgId();
                orgTreeAndUserVOList.add(orgTreeAndUserVO);
            }
            deptJosn = treeDingDeptAndUserList2(orgTreeAndUserVOList, 0);
            recursionSetUser(deptJosn);
        } catch (Exception e) {
            logger.error("getAllOrgTreeAndUser ERROR:", e);
            VoHelper.getErrorResult();
        }
        return VoHelper.getSuccessResult(deptJosn);
    }

    @Override
    public ResultVO getAllOrgTreeAndUserV2() {
        List<Organization> orgList = organizationMapper.queryAllDept();
        List<OrgTreeAndUserVO> lstAllUser = iUserMapper.getAllUser();
        lstAllUser.forEach(u -> u.isUser = 1);

        Organization org = orgList.stream().filter(o -> StringUtility.stringEqualsIgnoreCase(o.getParentDingOrgId(), "0")).findFirst().get();
        OrgTreeAndUserVO root = convertVO(org);
        handleChidrenOrg(root, orgList, lstAllUser);
        List<OrgTreeAndUserVO> deptJosn = new ArrayList<>();
        deptJosn.add(root);
        return VoHelper.getSuccessResult(deptJosn);
    }

    private void handleChidrenOrg(OrgTreeAndUserVO root, List<Organization> orgList, List<OrgTreeAndUserVO> lstAllUser) {
        root.children = orgList.stream().filter(o -> o.getParentDingOrgId().equalsIgnoreCase(root.key)).map(c -> convertVO(c)).collect(Collectors.toList());
        // ??????
        root.children.addAll(lstAllUser.stream().filter(u -> u.dingOrgId.equalsIgnoreCase(root.dingOrgId)).collect(Collectors.toList()));
        root.children.forEach(c ->
        {
            if (c.isUser == 0) {
                handleChidrenOrg(c, orgList, lstAllUser);
            }
        });
    }

    private OrgTreeAndUserVO convertVO(Organization org) {
        OrgTreeAndUserVO orgTreeAndUserVO = new OrgTreeAndUserVO();
        orgTreeAndUserVO.key = org.getDingOrgId();
        orgTreeAndUserVO.dingOrgId = org.getDingOrgId();
        orgTreeAndUserVO.title = org.getOrgName();
        //??????????????????0??????????????????
        orgTreeAndUserVO.isUser = 0;
        orgTreeAndUserVO.parentDingOrgId = org.getParentDingOrgId();
        return orgTreeAndUserVO;
    }

    @Override
    public ResultVO getUserByUserName(String operator, UserVO userVo) {
        try {
            //?????????????????????????????????
            if (!roleMapper.isAdminAccount(operator)) {
                userVo.createBy = operator;
            }
            UserDO userDO = userMapper.getUserByUserName(userVo);
            userVo.userName = userDO.getUserName();
            return VoHelper.getSuccessResult(userVo);
        } catch (Exception e) {
            return VoHelper.getErrorResult();
        }
    }

    //??????????????????
    private JSONArray treeDingDeptList(List<OrgVO> deptList, long parentId) {
        JSONArray childMenu = new JSONArray();
        for (OrgVO dept : deptList) {
            JSONObject jsonMenu = JSONObject.parseObject(JSON.toJSONString(dept));
            long id = jsonMenu.getLong("dingOrgId");
            long pid = jsonMenu.getLong("parentDingOrgId");
            if (parentId == pid) {
                JSONArray c_node = treeDingDeptList(deptList, id);
                jsonMenu.put("subOrg", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }

    private void recursionSetUser(List<OrgTreeAndUserVO> deptJosn) {
        if (CollectionUtils.isEmpty(deptJosn)) return;
        for (OrgTreeAndUserVO mem : deptJosn) {
            if (mem == null) continue;
            if (mem.children == null) mem.children = new ArrayList<>();
            recursionSetUser(mem.children);

            //???urc_person_org?????????ding_user_id
            List<String> dingUserIds = personOrgMapper.selectDingUserIdByDingOrgId(StringUtility.convertToLong(mem.key));
            //???urc_user?????????user_name
            if (!CollectionUtils.isEmpty(dingUserIds)) {
                List<UserAndPersonDO> userAndPersonDOS = iUserMapper.selectUserNameAndPeronNameByDingUserId(dingUserIds);
                List<OrgTreeAndUserVO> orgTreeAndUsers = new ArrayList<>();
                for (UserAndPersonDO userAndPersonDO : userAndPersonDOS) {
                    OrgTreeAndUserVO orgTreeAndUserVO = new OrgTreeAndUserVO();
                    orgTreeAndUserVO.isUser = 1;
                    // StringBuilder stringBuilder=new StringBuilder();
                    //orgTreeAndUserVO.key = stringBuilder.append(userAndPersonDO.userName).append("__").append(mem.key).toString();
                    orgTreeAndUserVO.key = userAndPersonDO.userName;
                    orgTreeAndUserVO.title = userAndPersonDO.personName;
                    orgTreeAndUsers.add(orgTreeAndUserVO);
                }
                mem.children.addAll(orgTreeAndUsers);
            }
        }
    }

    private List<OrgTreeAndUserVO> treeDingDeptAndUserList2(List<OrgTreeAndUserVO> orgTreeAndUserVOList, long parentId) {
        List<OrgTreeAndUserVO> childMenu = new ArrayList<>();
        for (OrgTreeAndUserVO dept1 : orgTreeAndUserVOList) {
            OrgTreeAndUserVO dept = StringUtility.parseObject(StringUtility.toJSONString_NoException(dept1), OrgTreeAndUserVO.class);
            long id = StringUtility.convertToLong(dept.key);
            long pid = StringUtility.convertToLong(dept.parentDingOrgId);
            if (parentId == pid) {
                List<OrgTreeAndUserVO> c_node = treeDingDeptAndUserList2(orgTreeAndUserVOList, id);
                //?????????????????????children???size()???0??????????????????????????????????????????????????????????????????
                if (!CollectionUtils.isEmpty(c_node))
                    dept.children = c_node;
                childMenu.add(dept);
            }
        }
        return childMenu;
    }

    //?????????????????????
    private JSONArray treeDingDeptAndUserList(List<OrgTreeAndUserVO> orgTreeAndUserVOList, long parentId) {
        JSONArray childMenu = new JSONArray();
        for (OrgTreeAndUserVO dept : orgTreeAndUserVOList) {
            JSONObject jsonMenu = JSONObject.parseObject(JSON.toJSONString(dept));
            long id = jsonMenu.getLong("key");
            long pid = jsonMenu.getLong("parentDingOrgId");
            if (parentId == pid) {
                JSONArray c_node = treeDingDeptAndUserList(orgTreeAndUserVOList, id);
                //?????????????????????children???size()???0??????????????????????????????????????????????????????????????????
                if (c_node.size() == 0) {
                    //???urc_person_org?????????ding_user_id
                    List<String> dingUserIds = personOrgMapper.selectDingUserIdByDingOrgId(id);
                    //???urc_user?????????user_name
                    if (!CollectionUtils.isEmpty(dingUserIds)) {
                        List<UserAndPersonDO> userAndPersonDOS = iUserMapper.selectUserNameAndPeronNameByDingUserId(dingUserIds);
                        List<OrgTreeAndUserVO> orgTreeAndUsers = new ArrayList<>();
                        for (UserAndPersonDO userAndPersonDO : userAndPersonDOS) {
                            OrgTreeAndUserVO orgTreeAndUserVO = new OrgTreeAndUserVO();
                            orgTreeAndUserVO.isUser = 1;
                            orgTreeAndUserVO.key = userAndPersonDO.userName;
                            orgTreeAndUserVO.title = userAndPersonDO.personName;
                            orgTreeAndUsers.add(orgTreeAndUserVO);
                        }
                        jsonMenu.put("children", orgTreeAndUsers);
                    }
                }
                //????????????????????????
                else {
                    jsonMenu.put("children", c_node);
                }
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }

}
