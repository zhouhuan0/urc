
package com.yks.urc.service.impl;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserRoleDO;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.service.api.ICommonPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class CommonPermissionServiceImpl implements ICommonPermissionService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;

    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private IUserMapper userMapper;
    private final static String ROLE_NAME = "通用角色";

    @Override
    public void authorize() {
        //查找通用角色id
        RoleDO role = roleMapper.getByRoleName(ROLE_NAME);
        //删除以前通用角色的所有人
        userRoleMapper.deleteByRoleId(role.getRoleId());
        //获得所有人
        List<String> lstUserName = userMapper.findAll();
        int total = lstUserName.size();
        int pageSize = 500;
        if (total > pageSize) {
            int count = total / pageSize;
            int remainder = total % pageSize;
            if (remainder > 0) {
                count++;
            }
            for (int i = 0; i < count; i++) {
                if (total < (i + 1) * pageSize) {
                    dealUser(lstUserName.subList(i * pageSize, total), role.getRoleId());
                } else {
                    dealUser(lstUserName.subList(i * pageSize, (i + 1) * pageSize), role.getRoleId());
                }
            }
        } else {
            dealUser(lstUserName, role.getRoleId());
        }
    }

    /**
     * 批量处理用户
     *
     * @param lstUserName
     * @param roleId
     */
    private void dealUser(List<String> lstUserName, Long roleId) {
        List<UserRoleDO> userRoleDOS = new ArrayList<>();
        for (String userName : lstUserName) {
            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setUserName(userName);
            userRoleDO.setRoleId(roleId);
            userRoleDO.setCreateBy("System");
            userRoleDO.setCreateTime(new Date());
            userRoleDO.setModifiedBy("system");
            userRoleDO.setModifiedTime(new Date());
            userRoleDOS.add(userRoleDO);
        }
        if (!CollectionUtils.isEmpty(lstUserName)) {
            /*批量新增用户-角色关系数据*/
            userRoleMapper.insertBatch(userRoleDOS);
        }

        if (!lstUserName.isEmpty()) {
            /*去重*/
            lstUserName = removeDuplicate(lstUserName);
            //保存权限改变的用户
            // 改为由定时任务执行
            permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
        }
    }

    /**
     * 去重
     */
    private List<String> removeDuplicate(List<String> lstUserName) {
        HashSet h = new HashSet(lstUserName);
        lstUserName.clear();
        lstUserName.addAll(h);
        return lstUserName;
    }

}
