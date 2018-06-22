package com.yks.urc.service.impl;

import java.sql.RowIdLifetime;
import java.util.ArrayList;
import java.util.List;

import com.yks.urc.entity.UserDO;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
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

    @Override
    public ResultVO getAllOrgTree() {
        JSONArray deptJosn = null;
        try {
            List<Organization> orgList = organizationMapper.queryAllDept();
            List<OrgVO> orgListVO = new ArrayList<OrgVO>();
            for (Organization org:orgList) {
            	OrgVO orgVO=new OrgVO();
            	orgVO.dingOrgId=org.getDingOrgId();
            	orgVO.orgName=org.getOrgName();
            	orgVO.parentDingOrgId=org.getParentDingOrgId();
            	orgListVO.add(orgVO);
			}
            
            deptJosn = treeDingDeptList(orgListVO, 0);
        } catch (Exception e) {
            VoHelper.getErrorResult();
        }

        return VoHelper.getSuccessResult(deptJosn);
    }

    @Override
    public ResultVO getUserByUserName(String operator, UserVO userVo) {
        try {
            //只需要查找用户的域账号
        	if(!roleMapper.isAdminAccount(operator)){
        		userVo.createBy=operator;
        	}
            List<UserDO> userDO = userMapper.getUserByUserName(userVo);
            return VoHelper.getSuccessResult(userVo);
        } catch (Exception e) {
            return VoHelper.getErrorResult();
        }
    }

    //菜单树形结构
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

}
