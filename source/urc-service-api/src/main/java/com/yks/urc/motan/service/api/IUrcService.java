package com.yks.urc.motan.service.api;

import java.util.Map;

import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

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
    String login(Map<String, String> map);


    /**
     * 手动触发“同步钉钉部门及人员”信息
     *
     * @param str
     * @return
     */
    String syncDingOrgAndUser();


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

}
