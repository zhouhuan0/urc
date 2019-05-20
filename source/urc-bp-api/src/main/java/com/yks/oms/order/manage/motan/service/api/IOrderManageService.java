package com.yks.oms.order.manage.motan.service.api;

import java.util.Map;

import com.yks.urc.vo.ResultVO;

public interface IOrderManageService {

    /**
     * @Description :获取所有平台销售账号
     * @Author: tangjianbo@youkeshu.com
     * @Date: 2018/12/18 12:21
     * @Param:
     * @return:
     **/

    ResultVO getAllSellerId();
    
    
    /**
     * @Description: 通过平台获取所有销售账号
     * @author: zengzheng
     * @param platformCode
     * @return
     * @version: 2019年5月17日 下午4:53:20
     * @param keys 
     */
    ResultVO getAllSellerIdByConditions(String platformCode, String keys);


    /**
     * @Description: 获取oms系统所有平台编码
     * @author: zengzheng
     * @return
     * @version: 2019年5月20日 上午11:49:53
     */
	ResultVO getAllOnlinePlatformCode();
}
