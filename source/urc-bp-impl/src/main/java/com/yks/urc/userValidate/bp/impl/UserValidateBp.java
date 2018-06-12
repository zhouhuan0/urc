package com.yks.urc.userValidate.bp.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.FunctionVO;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.PageVO;
import com.yks.urc.vo.SystemRootVO;

@Component
public class UserValidateBp implements IUserValidateBp {
	@Autowired
	IRoleMapper roleMapper;

	/**
	 * 根据username/syskey获取角色功能权限json
	 * 
	 * @param userName
	 * @param sysKey
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月12日 下午2:36:05
	 */
	public List<String> getFuncJsonByUserAndSysKey(String userName, String sysKey) {
		return roleMapper.getFuncJsonByUserAndSysKey(userName, sysKey);
	}

	public static void main(String[] args) throws IOException {
		// 读取func1.json文件
		String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func1.json"));
		SystemRootVO sys1 = StringUtility.parseObject(strJson1, SystemRootVO.class);

		// 读取func2.json文件
		String strJson2 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func2.json"));
		SystemRootVO sys2 = StringUtility.parseObject(strJson2, SystemRootVO.class);

		System.out.println("合并前的sys1:" + StringUtility.toJSONString_NoException(sys1));
		// sys2中的功能权限合并到sys1中
		distinctSystemRootVO(sys1, sys2);
		System.out.println("合并后的sys1:" + StringUtility.toJSONString_NoException(sys1));
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
		if (sys1.system.menu == null)
			sys1.system.menu = new ArrayList<>();
		if (sys2.system.menu == null)
			sys2.system.menu = new ArrayList<>();
		List<MenuVO> menu1 = sys1.system.menu;
		List<MenuVO> menu2 = sys2.system.menu;

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
		if (menu1.page == null)
			menu1.page = new ArrayList<>();
		if (menu2.page == null)
			menu2.page = new ArrayList<>();

		List<PageVO> page1 = menu1.page;
		List<PageVO> page2 = menu2.page;
		distinctPages(page1, page2);
	}

	private static void distinctPages(List<PageVO> page1, List<PageVO> page2) {
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

	private static void distinctPage(PageVO page1, PageVO page2) {
		// 合并page下的pages
		distinctPages(page1.page, page2.page);
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
}
