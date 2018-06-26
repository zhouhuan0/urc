package com.yks.urc.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.mapper.IUserPermitStatMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IRolePermissionMapper;
import com.yks.urc.mapper.IUserPermissionCacheMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
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
	private IUserRoleMapper userRoleMapper;
	
	@Autowired
	IRoleMapper roleMapper;
	
	@Autowired
	IRolePermissionMapper rolePermissionMapper;

	@Autowired
	private IUserPermitStatMapper userPermitStatMapper;
	
	@Autowired
	IOperationBp operationBp;

	@Autowired
	ICacheBp cacheBp;
	
	private IUserPermissionCacheMapper permissionCacheMapper;
	@Autowired
	private IUserValidateBp userValidateBp;

	@Transactional
	@Override
	public ResultVO importSysPermit(String jsonStr) {
		ResultVO rslt = new ResultVO();
		try {
			JSONObject jsonObject = StringUtility.parseString(jsonStr);
			//获取操作人
			String operator =jsonObject.get("operator").toString();
			//获取密码
			String pwd =jsonObject.get("pwd").toString();
			//获取数据
			String data = jsonObject.get("data").toString();
			if (StringUtility.isNullOrEmpty(operator)){
				return VoHelper.getErrorResult();
			}
			//判断密码是否相等
			if (!aesPwd.equals(pwd)) {
				return VoHelper.getErrorResult();
			}
			SystemRootVO[] arr = StringUtility.parseObject(data, new SystemRootVO[0].getClass());
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
				operationBp.addLog(PermissionServiceImpl.class.getName(), String.format("导入功能权限:%s", data), null);

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
		return rslt;
	}

	@Override
	public ResultVO getUserAuthorizablePermission(String userName) {
		List<PermissionVO> permissionVOs=new ArrayList<PermissionVO>();
		if(roleMapper.isSuperAdminAccount(userName)){
			//查询所有的角色功能权限
			List<Permission> lstSysKey = permissionMapper.getAllSysKey();
			for (Permission permission : lstSysKey) {
				PermissionVO permissionVO = new PermissionVO();
	        	permissionVO.setSysKey(permission.getSysKey());
	        	permissionVO.setSysContext(permission.getSysContext());
				permissionVOs.add(permissionVO);
			}
		}else{
			//业务管理员
			List<String> lstSysKey = userRoleMapper.getSysKeyByUser(userName);
			for (String sysKey : lstSysKey) {
				// 获取用户sys的功能权限json
				List<String> lstFuncJson = roleMapper.getFuncJsonByUserAndSysKey(userName, sysKey);
				// 合并json树
				SystemRootVO rootVO = userValidateBp.mergeFuncJson2Obj(lstFuncJson);
				PermissionVO permissionVO = new PermissionVO();
	        	permissionVO.setSysKey(sysKey);
	        	permissionVO.setSysContext(StringUtility.toJSONString_NoException(rootVO));
				permissionVOs.add(permissionVO);
			}
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
		checkAndConvertParam(queryMap, jsonObject);
        /*3、查询数据群贤模板列表信息*/
		List<UserPermitStatDO> userPermitStatDOS = userPermitStatMapper.listUserPermitStatsByPage(queryMap);
        /*4、List<DO> 转 List<VO>*/
		List<UserPermitStatVO> userPermitStatVOs = convertUserPermitStatDoToVO(userPermitStatDOS);
        /*5、获取总条数*/
		Long total = userPermitStatMapper.getCounts(queryMap.get("userName").toString());
		PageResultVO pageResultVO = new PageResultVO(userPermitStatVOs, total,queryMap.get("pageSize").toString());
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
	private void checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
         /*获取当前用户*/
		String userName = jsonObject.getString("operator");
		if (StringUtility.isNullOrEmpty(userName)) {
			throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
		}
		queryMap.put("userName", userName);
		String pageNumber = jsonObject.getString("pageNumber");
		String pageData = jsonObject.getString("pageData");
		if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
			throw new URCBizException("parameter operator is null", ErrorCode.E_000003);
		}
		int currPage = Integer.valueOf(pageNumber);
		int pageSize = Integer.valueOf(pageData);
		queryMap.put("currIndex", (currPage - 1) * pageSize);
		queryMap.put("pageSize", pageSize);



	}

}
