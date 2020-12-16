
package com.yks.urc.service.impl;

import com.yks.urc.config.bp.api.IConfigBp;
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
    @Autowired
    private IConfigBp configBp;
    private final static String ROLE_NAME = "ROLE_NAME";

    @Override
    public void authorize() {
        //获得角色名
        String roleName = configBp.getString(ROLE_NAME);
        if(null == roleName){
            logger.info("CommonPermissionServiceImpl:名称不能为空");
            return;
        }
        //查找通用角色id
        RoleDO role = roleMapper.getByRoleName(roleName);
        logger.info("CommonPermissionServiceImpl-名称为：" + roleName);
        if(null == role){
            return;
        }
        //获得已分配权限的人
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(role.getRoleId());
        List<String> permissionList = userRoleMapper.getUserNameByRoleId(userRoleDO);
        //获得所有人
        List<String> lstUserName = userMapper.findAll();
        //要删除的
        List<String> deleteList = getData(permissionList, lstUserName);
        if(!CollectionUtils.isEmpty(deleteList)) {
            UserRoleDO userRole = new UserRoleDO();
            userRole.setRoleId(role.getRoleId());
            userRoleMapper.deleteUserRoleInUserName(userRole, deleteList);
        }
        //要添加的
        List<String> addList = getData(lstUserName, permissionList);
        if(!CollectionUtils.isEmpty(addList)) {
            int total = addList.size();
            int pageSize = 500;
            if (total > pageSize) {
                int count = total / pageSize;
                int remainder = total % pageSize;
                if (remainder > 0) {
                    count++;
                }
                for (int i = 0; i < count; i++) {
                    if (total < (i + 1) * pageSize) {
                        dealUser(addList.subList(i * pageSize, total), role.getRoleId());
                    } else {
                        dealUser(addList.subList(i * pageSize, (i + 1) * pageSize), role.getRoleId());
                    }
                }
            } else {
                dealUser(addList, role.getRoleId());
            }
        }
    }

    /**
     * 获得在listOne中不在listTwo的数据
     *
     * @param listOne
     * @param listTwo
     * @return
     */
    private List<String> getData(List<String> listOne, List<String> listTwo) {
        if (CollectionUtils.isEmpty(listOne)) {
            return null;
        }
        if (CollectionUtils.isEmpty(listTwo)) {
            return listOne;
        }
        List<String> result = new ArrayList<String>();
        for (String str : listOne) {
            if (!listTwo.contains(str)) {
                result.add(str);
            }
        }
        return result;
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
