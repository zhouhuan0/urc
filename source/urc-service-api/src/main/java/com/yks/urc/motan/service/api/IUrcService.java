package com.yks.urc.motan.service.api;

import java.util.List;
import java.util.Map;

import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import com.yks.urc.vo.*;

public interface IUrcService {
    /**
     * 同步数据
     *
     * @param curUser
     * @return
     */
    String syncUserInfo(UserVO curUser);

    /**
     * 登陆校验：用户名密码检验
     *
     * @param curUser
     * @param authUser
     * @return
     * @author panyun@youkeshu.com
     * @date 2018年6月6日 下午12:22:35
     */
    ResultVO login(Map<String, String> map);

    /**
     * 校验ticket及功能权限版本
     * @param map
     * @return
     * @author panyun@youkeshu.com
     * @date 2018年6月14日 下午1:11:56
     */
    ResultVO funcPermitValidate(Map<String,String> map);

    /**
     * 手动触发“同步钉钉部门及人员”信息
     *
     * @param str
     * @return
     */
    String syncDingOrgAndUser();
    
    /**
     * 同步userInfo数据
     * @return
     */
    String syncUserInfo();

    /**
     * 用户管理搜索用户
     * @return
     */
    String getUsersByUserInfo(String params);
    
    
    /**
     * 根据DingOrgId获取人员信息含有分页
     *
     * @param str
     * @return
     */
    String getUserByDingOrgId(String params);


    /**
     * 组织架构搜索用户
     *
     * @param params
     * @return
     */
    String getUserByUserInfo(String params);

    /**
     * 组织结构树
     *
     * @param params
     * @return
     */
    String getAllOrgTree();

    /**
     * 快速分配数据权限模板给用户
     *
     * @param jsonStr
     * @return
     */
    String assignDataRuleTempl2User(String jsonStr);

    /**
     * 根据templId获取数据权限模板
     *
     * @param jsonStr
     * @return
     */
    String getDataRuleTemplByTemplId(String jsonStr);

    /**
     * 获取数据权限模板
     *
     * @param jsonStr
     * @return
     */
    String getDataRuleTempl(String jsonStr);
    

    /**
     *  获取所有平台
     * @param operator
     * @return
     */
    ResultVO<List<OmsPlatformVO>> getPlatformList(String operator);

    /**
     *  获取指定平台下的店铺名称和站点
     * @param operator
     * @param platform
     * @return
     */
    ResultVO<List<OmsAccountVO>> getShopList(String operator, String platform);
	/**
	 * 角色名判重
	 * @param operator
	 * @param newRoleName
	 * @param roleId 可为空，若为修改情况下，会排除自身进行校重
	 * @return 返回值为0--表示不重复, 1--表示重复
	 * @author oujie@youkeshu.com
	 */
    ResultVO<Integer> checkDuplicateRoleName(String operator, String newRoleName, String roleId);

    /**
     * 获取应用系统及其授权方式
     * @param  operator
     * @return  ResultVO<List<SysAuthWayVO>>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/14 14:31
     */
    String getMyAuthWay(String operator);




	/**
	 * 获取功能权限及版本号
	 * @param jsonStr
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午12:45:36
	 */
    ResultVO<List<UserSysVO>> getAllFuncPermit(String jsonStr);


	/**
	 * 获取角色关联的用户
	 * @param jsonStr
	 * @return
	 */
	String getUserByRoleId(String jsonStr);

	/**
	 * 获取多个角色已有的用户
	 * @param jsonStr
	 * @return
	 */
	String getRoleUser(String jsonStr);

	/**
	 * 获取用户可选择的所有数据授权方案
	 * @param jsonStr
	 * @return
	 */
	String getMyDataRuleTempl(String jsonStr);



	/**
	 * 获取多个用户的所有数据权限
	 * @param jsonStr
	 * @return
	 */
	String getDataRuleByUser(String jsonStr);

	/**
	 * 导入sys功能权限定义
	 * @param jsonStr
	 * @return
	 * @author panyun@youkeshu.com
	 * @date 2018年6月14日 下午7:17:14
	 */
	String importSysPermit(String jsonStr);

	/**
	 * 获取指定用户可授权给其它角色的功能权限
	 * @param jsonStr
	 * @return
	 */
	String getUserAuthorizablePermission(String jsonStr);

	/**
	 * 获取多个角色已有的功能权限
	 * @param jsonStr
	 * @return
	 */
	String getRolePermission(String jsonStr);
	
	/**
	 * 快速分配数据权限-模糊搜索用户域账号 
	 * @param jsonStr
	 * @return
	 */
	String getUserByUserName(String jsonStr);
}
