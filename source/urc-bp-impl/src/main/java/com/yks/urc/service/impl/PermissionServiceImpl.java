package com.yks.urc.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.crud.jdbc.Transactional;
import com.yks.urc.entity.Permission;
import com.yks.urc.fw.EncryptHelper;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class PermissionServiceImpl implements IPermissionService {

	@Value("${importSysPermit.aesPwd}")
	private String aesPwd;

	@Autowired
	PermissionMapper permissionMapper;

	@Autowired
	IOperationBp operationBp;

	@Transactional
	@Override
	public String importSysPermit(String jsonStr) {
		ResultVO rslt = new ResultVO();
		try {
			String plainTxt = StringUtility.Empty;
			try {
				plainTxt = EncryptHelper.decryptAes_Base64(jsonStr, aesPwd);
			} catch (UnsupportedEncodingException e) {
				rslt.state = CommonMessageCodeEnum.FAIL.getCode();
				rslt.msg = "解密失败";
			}
			// ResultVO<ArrayList<SystemRootVO>> request = new ResultVO<>();
			// request = StringUtility.parseObject(plainTxt, request.getClass());

			SystemRootVO[] arr = StringUtility.parseObject(plainTxt, new SystemRootVO[0].getClass());
			if (arr != null && arr.length > 0) {
				List<Permission> lstPermit = new ArrayList<>(arr.length);
				for (SystemRootVO root : arr) {
					Permission p = new Permission();
					p.setSysName(root.system.name);
					p.setSysKey(root.system.key);
					p.setSysContext(StringUtility.toJSONString_NoException(root));
					p.setCreateTime(new Date());
					lstPermit.add(p);
				}
				permissionMapper.deleteSyspermitDefine(lstPermit);
				permissionMapper.insertSysPermitDefine(lstPermit);
				operationBp.addLog(PermissionServiceImpl.class.getName(), String.format("导入功能权限:%s", plainTxt), null);
				rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
			} else {
				rslt.state = CommonMessageCodeEnum.FAIL.getCode();
				rslt.msg = "没有 data";
			}
		} catch (Exception ex) {
			rslt.state = CommonMessageCodeEnum.FAIL.getCode();
			rslt.msg = ex.getMessage();
		}
		return StringUtility.toJSONString_NoException(rslt);
	}

}
