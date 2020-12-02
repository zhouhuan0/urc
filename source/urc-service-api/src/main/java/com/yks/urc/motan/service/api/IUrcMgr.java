package com.yks.urc.motan.service.api;

import com.yks.urc.vo.*;

import java.util.List;
import java.util.Map;

public interface IUrcMgr {
    /**
     * 获取指定系统大于某个时间之后有更新的数据权限
     *
     * @param json
     * @return
     */
    ResultVO<List<DataRuleSysVO>> getDataRuleGtDt(String json);

    ResultVO checkSellerId(String jsonStr);
    /**
     * 快速分配数据权限模板给用户
     *
     * @param jsonStr
     * @return
     */
    ResultVO assignDataRuleTempl2User(String jsonStr) throws Exception;


    /**
     * 导入sys功能权限定义
     *
     * @param jsonStr
     * @return
     * @author panyun@youkeshu.com
     * @date 2018年6月14日 下午7:17:14
     */
    ResultVO importSysPermit(String jsonStr);


    ResultVO getRoleUserByRoleId(String json) throws Exception;

    /**
     * @Description 根据用户账号信息模糊查询对应用户的详细信息
     * @Author zengzheng
     * @Date 2020/5/21 16:13
     */
    ResultVO getUserInfoDetailByUserName(String json) throws Exception;

    /**
     * 获取多个角色已有的功能权限
     *
     * @param jsonStr
     * @return
     */
    ResultVO getRolePermission(String jsonStr);

    ResultVO getDepartment(String json);


    /**
     * @Description:  获取指定系统的平台编码
     * @author: zengzheng
     * @param jsonStr
     * @return
     * @version: 2019年5月20日 上午11:29:03
     */
    ResultVO getPlatformCode(String jsonStr);

    ResultVO addOrUpdateDataRule(String jsonStr);

    ResultVO updateRolePermission(String jsonStr);


    /**
     * @Description :搜索用户上网账号和用户名
     * @Author: tangjianbo@youkeshu.com
     * @Date: 2018/12/22 9:27
     * @Param:
     * @return:
     **/
    ResultVO searchUserPerson(String jsonStr);

    /**
     * 获取全部用户及组织结构
     *
     * @param
     * @return
     */
    ResultVO getAllOrgTreeAndUser();

    /**
     *精确匹配批量搜索用户账号
     * @param jsonStr
     * @return
     */
    ResultVO searchMatchUserPerson(String jsonStr);

    /**
     * 系统下拉框列表
     * @param jsonStr
     * @return
     */
    ResultVO getSystem(String jsonStr);

    /**
     * 获取系统的功能权限
     * @param jsonStr
     * @return
     */
    ResultVO getSystemPermission(String jsonStr);

    /**
     * 系统管理编辑
     * @param jsonStr
     * @return
     */
    ResultVO editSystemInfo(String jsonStr);

    /**
     * 获取系统信息列表
     * @param jsonStr
     * @return
     */
    ResultVO getSystemInfo(String jsonStr);

     /**
     *获取岗位用户列表
     * @param jsonStr
     * @return
     */
    ResultVO getUserByPosition(String jsonStr);

    /**
     *设置为超级管理员
     * @param jsonStr
     * @return
     */
    ResultVO setSupperAdmin(String jsonStr);

    /**
     *获取当前用户能授权的功能权限-岗位权限调用
     * @param jsonStr
     * @return
     */
    ResultVO getUserAuthorizablePermissionForPosition(String jsonStr);

    /**
     *保存岗位功能权限
     * @param jsonStr
     * @return
     */
    ResultVO savePositionPermission(String jsonStr);
}
