package com.yks.urc.permitStat.bp.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.excel.FileUpDownLoadUtils;
import com.yks.urc.excel.FileUploadRespVO;
import com.yks.urc.excel.PermitInfoExcelExport;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.mapper.IPermitItemInfoMapper;
import com.yks.urc.mapper.IPermitItemUserMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitInverseQueryBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.FunctionVO;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.ModuleVO;
import com.yks.urc.vo.PagedVO;
import com.yks.urc.vo.Req_getUserListByPermitKey;
import com.yks.urc.vo.RequestVO;
import com.yks.urc.vo.Resp_getUserListByPermitKey;
import com.yks.urc.vo.ResultPagedVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.UserSysVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class PermitInverseQueryBpImpl implements IPermitInverseQueryBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ISerializeBp serializeBp;
    
    private static String excelTemp = "/opt/tmp/";

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
                permitItemUserMapper.deleteByUserName(userName);
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
                    permitItemUserMapper.addOrUpdatePermitItemUser(lstAllKey.stream().collect(Collectors.toList()), userName);
                }
            } catch (Exception ex) {
                logger.error(userName, ex);
            }
        }
    }

    @Override
    public ResultVO getUserListByPermitKey(String json) {
        RequestVO<Req_getUserListByPermitKey> req = serializeBp.json2ObjNew(json, new TypeReference<RequestVO<Req_getUserListByPermitKey>>() {
        });
        if (req.data.lstPermitKey.size() > 5) {
            return VoHelper.getErrorResult(ErrorCode.E_000001.getState(), "lstPermitKey 参数不能大于5个元素");
        }
        ResultPagedVO<Resp_getUserListByPermitKey> rslt = new ResultPagedVO<>();
        rslt.data = new PagedVO<Resp_getUserListByPermitKey>();
        rslt.data.total = 0L;
        Long total = permitItemUserMapper.getUserListByPermitKeyTotal(req);
        if (total != null && total > 0) {
            rslt.data.total = total;
            req.data.offset = (req.data.pageNumber - 1) * req.data.pageData;
            List<Resp_getUserListByPermitKey> lstRslt = permitItemUserMapper.getUserListByPermitKey(req);
            rslt.data.list = lstRslt;
        }
        rslt.state = ErrorCode.E_000001.getState();
        return rslt;
    }

    @Autowired
    PermitInfoExcelExport permitInfoExcelExport;
    @Override
    public ResultVO exportUserListByPermitKey(String json) {
    	try {
    		RequestVO<Req_getUserListByPermitKey> req = serializeBp.json2ObjNew(json, new TypeReference<RequestVO<Req_getUserListByPermitKey>>() {
            });
            if (req.data.lstPermitKey.size() > 5) {
                return VoHelper.getErrorResult(ErrorCode.E_000001.getState(), "lstPermitKey 参数不能大于5个元素");
            }
            ResultPagedVO<Resp_getUserListByPermitKey> rslt = new ResultPagedVO<>();
            rslt.data = new PagedVO<Resp_getUserListByPermitKey>();
            rslt.data.total = 0L;
            Long total = permitItemUserMapper.getUserListByPermitKeyTotal(req);
            req.data.pageData = Integer.valueOf(total.toString());
            req.data.offset = 0;
            List<Resp_getUserListByPermitKey> lstRslt = permitItemUserMapper.getUserListByPermitKey(req);
            String downloadFileUrl = downloadByData(lstRslt);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(),downloadFileUrl);
		} catch (Exception e) {
			logger.error(String.format("exportUserListByPermitKey error ! json:%s", json),e);
		}
    	return VoHelper.getErrorResult();
    }

    private String downloadByData(List<Resp_getUserListByPermitKey> lstRslt) {
    	Date now = new Date();
    	String fileName = excelTemp + "permitKey-"+now.getTime() +".xlsx";
    	permitInfoExcelExport.setLstRslt(lstRslt);
    	permitInfoExcelExport.setExportFilePath(fileName);
    	permitInfoExcelExport.initExportExcel();
    	
    	String result = FileUpDownLoadUtils.getDownloadUrl("http://www.soter.youkeshu.com/yks/file/server/", fileName);
    	
		return result;
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
