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

}
