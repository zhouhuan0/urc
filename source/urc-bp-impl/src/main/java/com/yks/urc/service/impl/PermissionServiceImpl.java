package com.yks.urc.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.mapper.IUserPermitStatMapper;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.fw.EncryptHelper;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PermissionServiceImpl implements IPermissionService {

	private Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Value("${importSysPermit.aesPwd}")
	private String aesPwd;

	@Autowired
	PermissionMapper permissionMapper;
	
	@Autowired
	IRolePermissionMapper rolePermissionMapper;

	@Autowired
	private IUserPermitStatMapper userPermitStatMapper;
	
	@Autowired
	IOperationBp operationBp;

	@Autowired
	ICacheBp cacheBp;

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

				// 更新缓存
				for (Permission p : lstPermit) {
					cacheBp.insertSysContext(p.getSysKey(), p.getSysContext());
				}
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


	
	@Override
	public ResultVO getUserAuthorizablePermission(String userName) {
		//根据用户名获取角色的管理员
		List<RolePermissionDO> rolePermissionList=rolePermissionMapper.getUserAuthorizablePermission(userName);
		List<PermissionVO> permissionVOs=new ArrayList<PermissionVO>();
        for (RolePermissionDO rolePermissionDO : rolePermissionList) {
        	PermissionVO permissionVO = new PermissionVO();
        	permissionVO.setSysKey(rolePermissionDO.getSysKey());
        	permissionVO.setSysContext(rolePermissionDO.getSelectedContext());
            permissionVOs.add(permissionVO);
        }
		return VoHelper.getSuccessResult(permissionVOs);
	}

	/**
	 * Description: 查看功能权限--获取一个用户所有应用的功能权限（分页）
	 *
	 * @param :
	 * @return:
	 * @auther: lvcr
	 * @date: 2018/6/18 23:34
	 * @see
	 */
	@Override
	public ResultVO getUserPermissionList(String jsonStr) {
		 /*1、json字符串转json对象*/
		JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、请求参数的基本校验并转换为内部使用的Map*/
		Map<String, Object> queryMap = new HashMap<>();
		Boolean rtn = checkAndConvertParam(queryMap, jsonObject);
		if (!rtn) {
			return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
		}
        /*3、查询数据群贤模板列表信息*/
		List<UserPermitStatDO> userPermitStatDOS = userPermitStatMapper.listUserPermitStatsByPage(queryMap);
        /*4、List<DO> 转 List<VO>*/
		List<UserPermitStatVO> userPermitStatVOs = convertUserPermitStatDoToVO(userPermitStatDOS);
        /*5、获取总条数*/
		Long total = userPermitStatMapper.getCounts(queryMap.get("userName").toString());
		PageResultVO pageResultVO = new PageResultVO(userPermitStatVOs, total, Integer.valueOf(queryMap.get("pageSize").toString()));
		return VoHelper.getSuccessResult(pageResultVO);
	}

	private List<UserPermitStatVO> convertUserPermitStatDoToVO(List<UserPermitStatDO> userPermitStatDOS) {
		List<UserPermitStatVO> userPermitStatVOS = new ArrayList<>();
		for (UserPermitStatDO userPermitStatDO : userPermitStatDOS) {
			UserPermitStatVO userPermitStatVO = new UserPermitStatVO();
			BeanUtils.copyProperties(userPermitStatDO, userPermitStatVO);
			userPermitStatVO.setFuncDesc(userPermitStatDO.getFuncJson());
			userPermitStatVO.setSysName(userPermitStatDO.getPermission().getSysName());
			userPermitStatVOS.add(userPermitStatVO);
		}
		return userPermitStatVOS;
	}

	/**
	 * Description: 检查输入参数并转换
	 *
	 * @param :
	 * @return:
	 * @auther: lvcr
	 * @date: 2018/6/13 11:54
	 * @see
	 */
	private Boolean checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
         /*获取当前用户*/
		String userName = jsonObject.getString("operator");
		if (StringUtility.isNullOrEmpty(userName)) {
			logger.error("当期用户为空");
			return Boolean.FALSE;
		}
		queryMap.put("userName", userName);
		String pageNumber = jsonObject.getString("pageNumber");
		String pageData = jsonObject.getString("pageData");
		if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
			logger.error("分页参数有误");
			return Boolean.FALSE;
		}
		int currPage = Integer.valueOf(pageNumber);
		int pageSize = Integer.valueOf(pageData);
		queryMap.put("currIndex", (currPage - 1) * pageSize);
		queryMap.put("pageSize", pageSize);

		return Boolean.TRUE;


	}

}
