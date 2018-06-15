package com.yks.urc.userValidate.bp.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.weibo.api.motan.util.CollectionUtil;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.Permission;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserPermissionCacheDO;
import com.yks.urc.entity.UserPermitStatDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.FunctionVO;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.ModuleVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.UserVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class UserValidateBp implements IUserValidateBp {
	@Autowired
	IRoleMapper roleMapper;
	@Autowired
	ICacheBp cacheBp;

	@Autowired
	IOperationBp operationBp;

	public List<String> getFuncJsonLstByUserAndSysKey(String userName, String sysKey) {
		return roleMapper.getFuncJsonByUserAndSysKey(userName, sysKey);
	}

	/**
	 * 计算funcVersion
	 * 
	 * @param strFuncJson
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午3:56:19
	 */
	public String calcFuncVersion(String strFuncJson) {
		return StringUtility.md5_NoException(strFuncJson);
	}

	/**
	 * 打平所有module
	 * 
	 * @param sys1
	 * @author panyun@youkeshu.com
	 * @param userName
	 * @date 2018年6月12日 下午7:22:38
	 */
	public List<UserPermitStatDO> plainSys(SystemRootVO sys1, String userName) {
		List<MenuVO> lstMenu = sys1.menu;

		List<ModuleVO> lstModuleRslt = new ArrayList<>();
		for (MenuVO menu : lstMenu) {
			List<ModuleVO> lstModule = menu.module;
			if (lstModule == null)
				continue;
			for (ModuleVO m : lstModule) {
				m.pageFullPathName.append(m.name);
				m.lstChildFunc = getChildrenFuncDesc(m);
				m.sysKey = sys1.system.key;
				lstModuleRslt.add(m);
				plainModule(m, lstModuleRslt);
			}
		}

		List<UserPermitStatDO> lstRslt = new ArrayList<>();
		for (ModuleVO m : lstModuleRslt) {
			UserPermitStatDO statDo = new UserPermitStatDO();
			statDo.setUserName(userName);
			statDo.setSysKey(sys1.system.key);
			statDo.setModuleName(m.pageFullPathName.toString());
			statDo.setFuncJson(StringUtility.toJSONString_NoException(m.lstChildFunc));
			// System.out.println(m.sysKey + " " + m.pageFullPathName + " " +
			// StringUtility.toJSONString_NoException(m.lstChildFunc));
			lstRslt.add(statDo);
		}

		return lstRslt;
	}

	/**
	 * 按module节点打平
	 * 
	 * @param m
	 * @param lstModuleRslt
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午7:21:54
	 */
	private void plainModule(ModuleVO m, List<ModuleVO> lstModuleRslt) {
		if (m.module != null) {
			for (ModuleVO mem : m.module) {
				// if (mem.pageFullPathName == null)
				// mem.pageFullPathName = new StringBuilder();
				mem.pageFullPathName.append(m.pageFullPathName);
				mem.pageFullPathName.append("/");
				mem.pageFullPathName.append(mem.name);
				mem.lstChildFunc = getChildrenFuncDesc(mem);
				mem.sysKey = m.sysKey;
				lstModuleRslt.add(mem);
				plainModule(mem, lstModuleRslt);
			}
		}
	}

	/**
	 * 获取module所有子级function name
	 * 
	 * @param mem
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午7:09:28
	 */
	private List<String> getChildrenFuncDesc(ModuleVO mem) {
		if (mem == null || mem.function == null || mem.function.size() == 0)
			return null;
		List<String> sbFunc = new ArrayList();
		for (FunctionVO f : mem.function) {
			calcFuncDesc(f, sbFunc);
		}
		return sbFunc;
	}

	/**
	 * 递归查询所有function name
	 * 
	 * @param f
	 * @param sbRslt
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午7:09:00
	 */
	private void calcFuncDesc(FunctionVO f, List<String> sbRslt) {
		sbRslt.add(f.name);
		if (f.function != null) {
			for (FunctionVO fMem : f.function) {
				calcFuncDesc(fMem, sbRslt);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// 读取func1.json文件
		String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func1.json"));
		String strJson2 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func2.json"));
		System.out.println(new UserValidateBp().cleanDeletedNode(strJson1, strJson2));
		
		
//		SystemRootVO sys1 = StringUtility.parseObject(strJson1, SystemRootVO.class);
//		System.out.println(StringUtility.toJSONString_NoException(sys1));
//		new UserValidateBp().plainSys(sys1, "panyun");

		// 读取func2.json文件
		// String strJson2 =
		// StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func2.json"));
		// SystemRootVO sys2 = StringUtility.parseObject(strJson2, SystemRootVO.class);
		//
		// System.out.println("合并前的sys1:" +
		// StringUtility.toJSONString_NoException(sys1));
		// // sys2中的功能权限合并到sys1中
		// distinctSystemRootVO(sys1, sys2);
		// System.out.println("合并后的sys1:" +
		// StringUtility.toJSONString_NoException(sys1));
	}

	/**
	 * 功能权限json清理：将old与newest对比，删除old中存在但newest不存在的node删除
	 * 
	 * @param strFuncJsonOld
	 * @param strFuncJsonNewest
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:26:43
	 */
	public String cleanDeletedNode(String strFuncJsonOld, String strFuncJsonNewest) {
		SystemRootVO sysOld = StringUtility.parseObject(strFuncJsonOld, SystemRootVO.class);
		SystemRootVO sysNewest = StringUtility.parseObject(strFuncJsonNewest, SystemRootVO.class);

		// 将newest中的所有key存入List
		List<String> lstNewestKey = new ArrayList<>();
		if (sysNewest.menu != null) {
			for (MenuVO m : sysNewest.menu) {
				lstNewestKey.add(m.key);
				searchModuleKey(m.module, lstNewestKey);
			}
		}

		// 若old中的key在List中不存在，则删除
		// 判断 menu是否存在,不存在则删除
		if (sysOld.menu != null) {
			for (int i = 0; i < sysOld.menu.size(); i++) {
				if (lstNewestKey.contains(sysOld.menu.get(i).key))
					continue;
				// 删除menu
				sysOld.menu.remove(i);
				i--;
			}
		}

		for (MenuVO m : sysOld.menu) {
			cleanDeletedModule(m.module, lstNewestKey);
		}
		
		return StringUtility.toJSONString_NoException(sysOld);
	}

	/**
	 * 递归删除module下不存在的子node
	 * 
	 * @param lstModule
	 * @param lstNewestKey
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:53:44
	 */
	private void cleanDeletedModule(List<ModuleVO> lstModule, List<String> lstNewestKey) {
		if (lstModule == null || lstNewestKey == null)
			return;
		for (int i = 0; i < lstModule.size(); i++) {
			if (lstNewestKey.contains(lstModule.get(i).key)) {
				// 递归删除module下不存在的子node
				cleanDeletedModule(lstModule.get(i), lstNewestKey);
				continue;
			}
			// 删除module
			lstModule.remove(i);
			i--;
		}
	}

	/**
	 * 递归删除module下不存在的子node
	 * 
	 * @param moduleVO
	 * @param lstNewestKey
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:46:27
	 */
	private void cleanDeletedModule(ModuleVO moduleVO, List<String> lstNewestKey) {
		if (moduleVO == null || lstNewestKey == null)
			return;

		cleanDeletedModule(moduleVO.module, lstNewestKey);
		cleanDeletedFunction(moduleVO.function, lstNewestKey);
	}

	/**
	 * 递归删除function下不存在的子node
	 * 
	 * @param lstFunc
	 * @param lstNewestKey
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:53:52
	 */
	private void cleanDeletedFunction(List<FunctionVO> lstFunc, List<String> lstNewestKey) {
		if (lstFunc == null || lstNewestKey == null)
			return;
		for (int i = 0; i < lstFunc.size(); i++) {
			if (lstNewestKey.contains(lstFunc.get(i).key)) {
				cleanDeletedFunction(lstFunc.get(i).function, lstNewestKey);
				continue;
			}
			lstFunc.remove(i);
			i--;
		}
	}

	/**
	 * 递归查找module的key
	 * 
	 * @param lstModule
	 * @param lstNewestKey
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:37:41
	 */
	private void searchModuleKey(List<ModuleVO> lstModule, List<String> lstNewestKey) {
		if (lstModule != null) {
			for (ModuleVO module : lstModule) {
				lstNewestKey.add(module.key);
				searchModuleKey(module.module, lstNewestKey);
				searchFunctionKey(module.function, lstNewestKey);
			}
		}
	}

	/**
	 * 递归查找function的key
	 * 
	 * @param lstFunc
	 * @param lstNewestKey
	 * @author panyun@youkeshu.com
	 * @date 2018年6月15日 上午8:37:28
	 */
	private void searchFunctionKey(List<FunctionVO> lstFunc, List<String> lstNewestKey) {
		if (lstFunc == null)
			return;
		for (FunctionVO f : lstFunc) {
			lstNewestKey.add(f.key);
			if (f.function != null) {
				searchFunctionKey(f.function, lstNewestKey);
			}
		}
	}

	/**
	 * sys2中的功能权限合并到sys1中
	 * 
	 * @param sys1
	 * @param sys2
	 * @author panyun@youkeshu.com
	 * @date 2018年5月26日 上午11:51:59
	 */
	private static void distinctSystemRootVO(SystemRootVO sys1, SystemRootVO sys2) {
		if (sys1.menu == null)
			sys1.menu = new ArrayList<>();
		if (sys2.menu == null)
			sys2.menu = new ArrayList<>();
		List<MenuVO> menu1 = sys1.menu;
		List<MenuVO> menu2 = sys2.menu;

		boolean hasSameMenu = false;
		for (int j = 0; j < menu2.size(); j++) {
			for (int i = 0; i < menu1.size(); i++) {
				if (StringUtility.stringEqualsIgnoreCase(menu1.get(i).key, menu2.get(j).key)) {
					// 合并相同menu
					distinctMenu(menu1.get(i), menu2.get(j));
					hasSameMenu = true;
					break;
				}
			}

			if (hasSameMenu) {
				// 有相同menu,前面已经做了menu合并,menu2删除一个元素
				menu2.remove(j);
				j--;
			} else {
				// 没有相同menu,menu2中的添加到menu1中，并删除menu2中当前元素
				menu1.add(menu2.get(j));
				menu2.remove(j);
				j--;
			}
		}
	}

	private static void distinctMenu(MenuVO menu1, MenuVO menu2) {
		// 合并menu下的page
		if (menu1.module == null)
			menu1.module = new ArrayList<>();
		if (menu2.module == null)
			menu2.module = new ArrayList<>();

		List<ModuleVO> page1 = menu1.module;
		List<ModuleVO> page2 = menu2.module;
		distinctPages(page1, page2);
	}

	private static void distinctPages(List<ModuleVO> page1, List<ModuleVO> page2) {
		if (page1 == null)
			page1 = new ArrayList<>();
		if (page2 == null)
			page2 = new ArrayList<>();
		boolean hasSameMenu = false;
		for (int j = 0; j < page2.size(); j++) {
			for (int i = 0; i < page1.size(); i++) {
				if (StringUtility.stringEqualsIgnoreCase(page1.get(i).key, page2.get(j).key)) {
					// 合并相同page
					distinctPage(page1.get(i), page2.get(j));
					hasSameMenu = true;
					break;
				}
			}

			if (hasSameMenu) {
				// 有相同page,前面已经做了page合并,page2删除一个元素
				page2.remove(j);
				j--;
			} else {
				// 没有相同page,page2中的添加到page1中，并删除page2中当前元素
				page1.add(page2.get(j));
				page2.remove(j);
				j--;
			}
		}
	}

	private static void distinctPage(ModuleVO page1, ModuleVO page2) {
		// 合并page下的pages
		distinctPages(page1.module, page2.module);
		// 合并page下的functions
		distinctFunctions(page1.function, page2.function);
	}

	private static void distinctFunctions(List<FunctionVO> functions1, List<FunctionVO> functions2) {
		if (functions1 == null)
			functions1 = new ArrayList<>();
		if (functions2 == null)
			functions2 = new ArrayList<>();
		boolean hasSameMenu = false;
		for (int j = 0; j < functions2.size(); j++) {
			for (int i = 0; i < functions1.size(); i++) {
				if (StringUtility.stringEqualsIgnoreCase(functions1.get(i).key, functions2.get(j).key)) {
					// 合并相同functions
					distinctFunction(functions1.get(i), functions2.get(j));
					hasSameMenu = true;
					break;
				}
			}

			if (hasSameMenu) {
				// 有相同page,前面已经做了page合并,page2删除一个元素
				functions2.remove(j);
				j--;
			} else {
				// 没有相同page,page2中的添加到page1中，并删除page2中当前元素
				functions1.add(functions2.get(j));
				functions2.remove(j);
				j--;
			}
		}
	}

	private static void distinctFunction(FunctionVO function1, FunctionVO function2) {
		// 合并function下的functions
		distinctFunctions(function1.function, function2.function);
	}

	@Override
	public String createTicket(String strUserName, String ip) {
		return StringUtility.md5_NoException(String.format("%s%s%s", strUserName, ip, StringUtility.getUUIDLowercase_Dt()));

	}

	@Override
	public String getFuncJsonByUserAndSysKey(String userName, String sysKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mergeFuncJson(List<String> lstJson) {
		return StringUtility.toJSONString_NoException(mergeFuncJson2Obj(lstJson));
	}

	@Override
	public SystemRootVO mergeFuncJson2Obj(List<String> lstJson) {
		if (lstJson == null || lstJson.size() == 0)
			return null;
		SystemRootVO sys1 = StringUtility.parseObject(lstJson.get(0), SystemRootVO.class);
		for (int i = 1; i < lstJson.size(); i++) {
			String strMem = lstJson.get(i);
			// sys2中的功能权限合并到sys1中
			SystemRootVO sys2 = StringUtility.parseObject(strMem, SystemRootVO.class);

			distinctSystemRootVO(sys1, sys2);
		}
		return sys1;
	}

	@Override
	public ResultVO funcPermitValidate(Map<String, String> map) {
		operationBp.addLog(UserValidateBp.class.getName(), StringUtility.toJSONString_NoException(map), null);
		String apiUrl = map.get("apiUrl");
		String moduleUrl = map.get("moduleUrl");
		String operator = map.get(StringConstant.operator);
		String ticket = map.get(StringConstant.ticket);
		String ip = map.get(StringConstant.ip);
		String urcVersion = map.get(StringConstant.urcVersion);
		String sysKey = map.get(StringConstant.sysKey);

		UserVO u = cacheBp.getUser(operator);
		// 校验ticket
		if (u == null || !StringUtility.stringEqualsIgnoreCase(u.ticket, ticket) || !StringUtility.stringEqualsIgnoreCase(u.ip, ip)) {
			// 100002
			return VoHelper.getResultVO("100002", "登录超时");
		}

		if (StringUtility.stringEqualsIgnoreCase("a2af9fccd4e40486", moduleUrl)) {
			return VoHelper.getResultVO(StringConstant.STATE_100006, "用户功能权限版本正确");
		}

		// 校验功能权限版本
		if (!StringUtility.stringEqualsIgnoreCase(urcVersion, getFuncVersionFromDbOrCache(operator, sysKey))) {
			return VoHelper.getResultVO("100007", "功能权限版本错误");
		}

		// 校验是否有权限
		if (!hasApiFunc(moduleUrl, apiUrl, operator, sysKey)) {
			return VoHelper.getResultVO("100003", "没有权限");
		}
		return VoHelper.getResultVO(StringConstant.STATE_100006, "用户功能权限版本正确");
	}

	@Autowired
	private IPermitStatBp permitStatBp;

	/**
	 * 从db或cache获取funcVersion
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午4:43:27
	 */
	public String getFuncVersionFromDbOrCache(String userName, String sysKey) {
		String funcVersion = cacheBp.getFuncVersion(userName, sysKey);
		if (funcVersion == null) {
			UserPermissionCacheDO ca = permitStatBp.updateUserPermitCache(userName, sysKey);
			if (ca != null)
				return ca.getPermissionVersion();
		}

		return funcVersion;
	}

	/**
	 * 判断是否有当前api权限
	 * 
	 * @param moduleUrl
	 * @param apiUrl
	 * @param operator
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午3:14:43
	 */
	private boolean hasApiFunc(String moduleUrl, String apiUrl, String operator, String sysKey) {
		// 获取业务系统的功能权限定义 jsonA
		String strSysFuncJson = cacheBp.getSysContext(sysKey);
		if (strSysFuncJson == null) {
			strSysFuncJson = this.getSysContextFromDb(sysKey);
			cacheBp.insertSysContext(sysKey, strSysFuncJson);
			if (StringUtility.isNullOrEmpty(strSysFuncJson))
				return true;
		}

		if (StringUtility.Empty.equals(strSysFuncJson))
			return true;

		// apiUrl在A不存在,返回true
		if (!strSysFuncJson.contains("\"" + apiUrl + "\""))
			return true;

		// 获取用户当前业务系统的funcJson
		String strCurUserFuncJson = cacheBp.getFuncJson(operator, sysKey);
		if (strCurUserFuncJson == null)
			return false;

		// apiUrl在SubA中存在，返回true
		if (strCurUserFuncJson.contains("\"" + apiUrl + "\""))
			return true;
		return false;
	}

	@Autowired
	PermissionMapper permissionMapper;

	private String getSysContextFromDb(String sysKey) {
		Permission per = permissionMapper.getPermissionBySysKey(sysKey);
		if (per == null || per.getSysContext() == null)
			return StringUtility.Empty;
		return per.getSysContext();
	}
}
