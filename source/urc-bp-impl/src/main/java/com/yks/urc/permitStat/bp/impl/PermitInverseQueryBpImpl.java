package com.yks.urc.permitStat.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPermitItemInfoMapper;
import com.yks.urc.mapper.IPermitItemUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitInverseQueryBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermitInverseQueryBpImpl implements IPermitInverseQueryBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ISerializeBp serializeBp;

    public void xxx() {
        Set<FunctionVO> lstAllKey = new HashSet<>();
        List<PermissionDO> lstPermission = permissionMapper.getAllSysPermit();
        for (PermissionDO permissionDO : lstPermission) {
            SystemRootVO rootVO = serializeBp.json2ObjNew(permissionDO.getSysContext(), new TypeReference<SystemRootVO>() {
            });

            scanMenu(permissionDO.getSysName(), rootVO, lstAllKey);
        }
        logger.info(serializeBp.obj2Json(lstAllKey));
    }

    @Autowired
    private IPermitItemInfoMapper permitItemInfoMapper;

    public void updatePermitItemInfo(List<String> lstSysKey) {
        if (CollectionUtils.isEmpty(lstSysKey)) {
            return;
        }
        for (String sysKey : lstSysKey) {
            if (StringUtils.isBlank(sysKey)) {
                continue;
            }
            PermissionDO permissionDO = permissionMapper.getPermissionBySysKey(sysKey);
            SystemRootVO rootVO = serializeBp.json2ObjNew(permissionDO.getSysContext(), new TypeReference<SystemRootVO>() {
            });
            Set<FunctionVO> lstAllKey = getAllPermitItem(rootVO);
            if (CollectionUtils.isEmpty(lstAllKey)) {
                continue;
            }
            permitItemInfoMapper.addOrUpdate(lstAllKey.stream().collect(Collectors.toList()), sessionBp.getOperator());
        }
    }

    @Autowired
    private ISessionBp sessionBp;
    @Autowired
    private IUserRoleMapper userRoleMapper;

    @Autowired
    private IPermitStatBp permitStatBp;

    @Autowired
    private IPermitItemUserMapper permitItemUserMapper;

    private Set<FunctionVO> getAllPermitItem(SystemRootVO rootVO) {
        Set<FunctionVO> lstAllKey = new HashSet<>();
        scanMenu(rootVO.system.name, rootVO, lstAllKey);
        return lstAllKey;
    }

    @Override
    public void doTaskSub(List<String> lstUser) {
//        List<String> lstUser = userRoleMapper.getAllUserName();
        for (String userName : lstUser) {
            try {
                GetAllFuncPermitRespVO respVO = permitStatBp.updateUserPermitCache(userName);
                if (respVO == null || CollectionUtils.isEmpty(respVO.lstUserSysVO)) {
                    continue;
                }
                permissionMapper.deleteByUserName(userName);
                Set<FunctionVO> lstAllKey = new HashSet<>();

                for (UserSysVO userSysVO : respVO.lstUserSysVO) {
                    if (userSysVO == null || StringUtils.isBlank(userSysVO.context)) {
                        continue;
                    }
                    SystemRootVO rootVO = serializeBp.json2ObjNew(userSysVO.context, new TypeReference<SystemRootVO>() {
                    });
                    lstAllKey.addAll(getAllPermitItem(rootVO));
//                    scanMenu(rootVO.system.name, rootVO, lstAllKey);
                }
                if (!CollectionUtils.isEmpty(lstAllKey)) {
                    permissionMapper.addOrUpdatePermitItemUser(lstAllKey.stream().collect(Collectors.toList()), userName);
                }
            } catch (Exception ex) {
                logger.error(userName, ex);
            }
        }
    }

    private void scanMenu(String sysName, SystemRootVO rootVO, Set<FunctionVO> lstAllKey) {
        List<MenuVO> menu1 = rootVO.menu;
        if (CollectionUtils.isEmpty(menu1)) {
            return;
        }
        for (int j = 0; j < menu1.size(); j++) {
            MenuVO curMemu = menu1.get(j);
//            lstAllKey.add(curMemu.key);
            scanModule(String.format("%s-%s", sysName, curMemu.name), curMemu.module, lstAllKey);
        }
    }

    private void scanModule(String parentName, List<ModuleVO> lstModule, Set<FunctionVO> lstAllKey) {
        if (CollectionUtils.isEmpty(lstModule)) {
            return;
        }
        for (ModuleVO moduleVO : lstModule) {
            if (CollectionUtils.isEmpty(moduleVO.module) && CollectionUtils.isEmpty(moduleVO.function)) {
                FunctionVO fKey = new FunctionVO();
                fKey.key = moduleVO.key;
                fKey.name = String.format("%s-%s", parentName, moduleVO.name);
                lstAllKey.add(fKey);
            } else {
                scanModule(String.format("%s-%s", parentName, moduleVO.name), moduleVO.module, lstAllKey);
                scanFunction(String.format("%s-%s", parentName, moduleVO.name), moduleVO.function, lstAllKey);
            }
        }
    }

    private void scanFunction(String parentName, List<FunctionVO> lstFunction, Set<FunctionVO> lstAllKey) {
        if (CollectionUtils.isEmpty(lstFunction)) {
            return;
        }
        for (FunctionVO f : lstFunction) {
            if (CollectionUtils.isEmpty(f.function)) {
                f.name = String.format("%s-%s", parentName, f.name);
                lstAllKey.add(f);
            } else {
                scanFunction(f.name, f.function, lstAllKey);
            }
        }
    }
}
