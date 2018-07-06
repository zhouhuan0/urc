/**
 * 〈一句话功能简述〉<br>
 * 〈获取平台,店铺,站点信息〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/12
 * @since 1.0.0
 */
package com.yks.urc.dataauthorization.bp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;

import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataAuthorizationImpl implements DataAuthorization {
    private static Logger logger = LoggerFactory.getLogger(DataAuthorizationImpl.class);
    /**
     * 请求平台url
     */
    @Value("${dataAuthorization.getPlatformList}")
    private String GET_PLATFORM;
    /**
     * 请求店铺账号和站点信息
     */
    @Value("${dataAuthorization.getShopList}")
    private String GET_SHOP_AND_SITE;

    /**
     * 获取平台信息
     *
     * @param operator
     * @return List<DataAuthorizationVo>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 20:52
     */
    @Override
    public List<OmsPlatformVO> getPlatformList(String operator) {
        List<OmsPlatformVO> omsPlatformVoList = new ArrayList<>();
        String getPlatformResult = HttpUtility.httpGet(GET_PLATFORM);
        //将拿到的结果转为json ,获取平台信息
        JSONObject platformObject = StringUtility.parseString(getPlatformResult);
        if (platformObject.getInteger("state") == 200) {
            JSONArray dataArray = platformObject.getJSONArray("data");
            List<PlatformResp> platformResps = StringUtility.jsonToList(dataArray.toString(), PlatformResp.class);
            for (PlatformResp platformResp : platformResps) {
                OmsPlatformVO omsPlatformVO = new OmsPlatformVO();
                omsPlatformVO.platformId = platformResp.code;
                // 如果id 为空,则不装载数据
                if (StringUtility.isNullOrEmpty(omsPlatformVO.platformId)) {
                    continue;
                }
                omsPlatformVO.platformName = platformResp.name;
                omsPlatformVoList.add(omsPlatformVO);
            }
        }
        return omsPlatformVoList;
    }

    /**
     * 获取平台下的站点和店铺信息
     *
     * @param operator
     * @param platform
     * @return List<DataAuthorizationVo>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 21:14
     */
    @Override
    public List<OmsShopVO> getShopList(String operator, String platform) {
        List<OmsShopVO> omsShopVoList = new ArrayList<>();
        // 将平台进行转码
        String platforms = URLEncoder.encode(platform);
        String url = GET_SHOP_AND_SITE + "&platform=" + platforms;
        String getShopAndSiteResult = HttpUtility.httpGet(url);
        if (StringUtility.isNullOrEmpty(getShopAndSiteResult)) {
            throw new URCBizException(CommonMessageCodeEnum.FAIL.getCode(),"获取站点信息为空");
        }
        JSONObject shopObject = StringUtility.parseString(getShopAndSiteResult);
        if (shopObject.getInteger("state") == 200) {
            JSONArray dataArray = shopObject.getJSONArray("data");
            List<ShopAndSiteResp> shopAndSiteResps = StringUtility.jsonToList(dataArray.toString(), ShopAndSiteResp.class);
            for (ShopAndSiteResp shopAndSiteResp : shopAndSiteResps) {
                OmsShopVO omsShopVO = new OmsShopVO();
                omsShopVO.shopId = shopAndSiteResp.sellerid;
                // 如果id 为空,则不装载数据
                if (StringUtility.isNullOrEmpty(omsShopVO.shopId)) {
                    continue;
                }
                omsShopVO.shopName = shopAndSiteResp.shop_system;
                // name没有, 将id赋值给name
                if (StringUtility.isNullOrEmpty(omsShopVO.shopName)) {
                    omsShopVO.shopName = omsShopVO.shopId;
                }
                // 如果获取的site_code 为空,则不装载数
               if (StringUtility.isNullOrEmpty(shopAndSiteResp.site_code)) {
                    omsShopVO.lstSite = null;
                } else {
                    omsShopVO.lstSite = new ArrayList<>();
                    OmsSiteVO omsSiteVO = new OmsSiteVO();
                    omsSiteVO.siteId = shopAndSiteResp.site_code;
                    omsSiteVO.siteName = shopAndSiteResp.site_name;
                    // name 没有 将id  赋值给name
                    if (StringUtility.isNullOrEmpty(omsSiteVO.siteName)) {
                        omsSiteVO.siteName = omsSiteVO.siteId;
                    }
                    omsShopVO.lstSite.add(omsSiteVO);
                }
                omsShopVoList.add(omsShopVO);
            }
        }
        return omsShopVoList;
    }

    public static void main(String[] args) {
        DataAuthorizationImpl dataAuthorizationImpl = new DataAuthorizationImpl();
        List<OmsPlatformVO> omsPlatformVOS = dataAuthorizationImpl.getPlatformList("test3");
        for (OmsPlatformVO omsPlatformVO : omsPlatformVOS) {
            System.out.println(omsPlatformVO.platformName);
        }
        System.out.println("============================");
        String platform = "eBay";
        List<OmsShopVO> omsShopVoList = dataAuthorizationImpl.getShopList("test3", platform);
        for (OmsShopVO omsShopVO : omsShopVoList) {
            System.out.println(omsShopVO.shopName);
        }
    }
}
