package com.yks.urc.permitStat.bp.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.yks.urc.entity.PermissionDO;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.permitStat.bp.api.IPermitSortBp;
import com.yks.urc.permitStat.bp.api.IPermitTreeRefreshBp;
import com.yks.urc.vo.SystemKeyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.entity.UserPermitStatDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserPermissionCacheMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.UserSysVO;
import org.springframework.util.CollectionUtils;

@Component
public class PermitStatBpImpl implements IPermitStatBp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IUserRoleMapper userRoleMapper;
	@Autowired
	IRoleMapper roleMapper;
	@Autowired
	private IUserPermissionCacheMapper permissionCacheMapper;
	@Autowired
	private IUserValidateBp userValidateBp;

	@Autowired
	private PermissionMapper permissionMapper;

	@Override
	public void updateUserPermitCache(List<String> lstUserName) {
		if (lstUserName == null || lstUserName.size() == 0) {
			return;
		}
		permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
		for (String userName : lstUserName) {
                Long startTime = System.currentTimeMillis();
                updateUserPermitCache(userName);
                Long endTime = System.currentTimeMillis();
                logger.info(String.format("updateUserPermitCache One 耗时 %s:%s ms", userName, (endTime - startTime)));
		}
	}

	@Autowired
	private IPermitRefreshTaskBp permitRefreshTaskBp;

	@Autowired
	private ICacheBp cacheBp;

	@Autowired
	private IPermitSortBp permitSortBp;

	@Autowired
	private IPermitTreeRefreshBp permitTreeRefreshBp;

	public GetAllFuncPermitRespVO updateUserPermitCache(String userName) {
		GetAllFuncPermitRespVO permitCache = new GetAllFuncPermitRespVO();

		try {
			// 获取用户所有的sysKey
			List<String> lstSysKey = userRoleMapper.getSysKeyByUser(userName);
			if (lstSysKey == null)
				lstSysKey = new ArrayList<>();

			List<UserPermissionCacheDO> lstCacheToAdd = new ArrayList<>(lstSysKey.size());
			permitCache.lstUserSysVO=new ArrayList<>();
			// 先删除冗余表数据
			permissionCacheMapper.deletePermitCacheByUser(userName);
			permissionCacheMapper.deletePermitStatByUser(userName);

			if (lstSysKey == null || lstSysKey.size() == 0) {
				// 清除缓存
//				cacheBp.removeUserSysKey(userName);
				// 更新缓存
//				cacheBp.insertUserSysKey(userName, lstSysKey);
				cacheBp.insertUserFunc(userName, permitCache);
				return permitCache;
			}

			List<UserPermitStatDO> lstStatToAdd = new ArrayList<>();
			//优化后
			List<SystemKeyContext> funcJsonList = roleMapper.getFuncJsonListByUserAndSysKey(userName, lstSysKey);
			//第一种
			if (!CollectionUtils.isEmpty(funcJsonList)) {
				Map<String,List<SystemKeyContext>> keyToContextMap = funcJsonList.stream().collect(Collectors.groupingBy(SystemKeyContext::getSysKey, Collectors.toList()));
				for(Map.Entry<String,List<SystemKeyContext>> entry: keyToContextMap.entrySet()){
					List<String> lstFuncJson = entry.getValue().stream().map(SystemKeyContext::getSelectedContext).collect(Collectors.toList());
					UserPermissionCacheDO cacheDo = new UserPermissionCacheDO();
					cacheDo.setUserName(userName);
					cacheDo.setSysKey(entry.getKey());
					// 合并json树
					SystemRootVO rootVO = userValidateBp.mergeFuncJson2Obj(lstFuncJson);
					if(null == rootVO) continue;
					//获取sysName
//					String sysName = getSysNameBySyskey(entry.getKey());
//					if (!StringUtility.isNullOrEmpty(sysName)) {
//						rootVO.system.name = sysName;
//					}

					// 更新所有节点的name/sortIdx/url
					// 因为角色下的json与 urc_permission 表最新json定义可能会不一样
					permitTreeRefreshBp.refreshNewestFields(rootVO);

//					if ("001".equalsIgnoreCase(entry.getKey())) {
						permitSortBp.sortSystemRootVO(rootVO, entry.getKey());
//					}


					cacheDo.setUserContext(StringUtility.toJSONString_NoException(rootVO));
					//				cacheDo.setPermissionVersion(userValidateBp.calcFuncVersion(cacheDo.getUserContext()));
					cacheDo.setCreateTime(new Date());
					cacheDo.setModifiedTime(cacheDo.getCreateTime());
					lstCacheToAdd.add(cacheDo);

					UserSysVO userPermit = new UserSysVO();
					userPermit.sysKey = entry.getKey();
					userPermit.context = cacheDo.getUserContext();
					permitCache.lstUserSysVO.add(userPermit);
					calcFuncVersion(permitCache);
					List<UserPermitStatDO> lstStatCur = userValidateBp.plainSys(rootVO, userName);
					if (lstStatCur != null && lstStatCur.size() > 0) {
						lstStatToAdd.addAll(lstStatCur);
					}
				}
			}

			if (lstCacheToAdd.size() > 0) {
				// insert urc_user_permission_cache表
				permissionCacheMapper.insertPermitCache(lstCacheToAdd);

			}
			if (lstStatToAdd.size() > 0) {
				// insert urc_user_permit_stat 表
				permissionCacheMapper.insertPermitStat(lstStatToAdd);
			}

			// 更新缓存
//			cacheBp.insertUserSysKey(userName, lstSysKey);
			
			cacheBp.insertUserFunc(userName, permitCache);
			return permitCache;
		} catch (Exception ex) {
			logger.error(String.format("updateUserPermitCache:%s", userName), ex);
		}
		return permitCache;
	}

	/**
	 *  通过sysKey 获取sysName
	 * @param
	 * @return
	 * @Author lwx
	 * @Date 2018/8/13 15:02
	 */
	private String getSysNameBySyskey(String sysKey) {
	String sysName= permissionMapper.getSysNameByKey(sysKey);
		if (StringUtility.isNullOrEmpty(sysName)) {
			return "";
		}else {
			return sysName;
		}
	}




	/**
	 * 所有系统的功能权限json做字符串相加，再计算md5
	 * @param permitCache
	 * @author panyun@youkeshu.com
	 * @date 2018年6月21日 下午4:21:32
	 */
	private void calcFuncVersion(GetAllFuncPermitRespVO permitCache) {
		if (permitCache == null || permitCache.lstUserSysVO == null || permitCache.lstUserSysVO.size() == 0)
			return;
		StringBuilder sb = new StringBuilder();
		for (UserSysVO u : permitCache.lstUserSysVO) {
			sb.append(u.context);
		}
		permitCache.funcVersion = userValidateBp.calcFuncVersion(sb.toString());
	}
}
