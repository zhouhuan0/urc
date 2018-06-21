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
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;

import com.yks.urc.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class DataAuthorizationImpl implements DataAuthorization {
    private static Logger logger = LoggerFactory.getLogger(DataAuthorizationImpl.class);
    /**
     * 请求平台url
     */
    @Value("${dataAuthorization.getPlatformList}")
    private static  String GET_PLATFORM ;
    /**
     * 请求店铺账号和站点信息
     */
    @Value("${dataAuthorization.getShopList}")
    private static  String GET_SHOP_AND_SITE;

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
            List<PlatformResp> platformResps= StringUtility.jsonToList(dataArray.toString(), PlatformResp.class);
            for(PlatformResp platformResp : platformResps){
                OmsPlatformVO omsPlatformVO =new OmsPlatformVO();
                omsPlatformVO.platformId =platformResp.code;
                omsPlatformVO.platformName=platformResp.name;
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
    public List<OmsAccountVO> getShopList(String operator, String platform) {
        List<OmsAccountVO> omsAccountVoList = new ArrayList<>();
        String url = GET_SHOP_AND_SITE + "&platform=" + platform;
        String getShopAndSiteResult = HttpUtility.httpGet(url);
        JSONObject shopObject = StringUtility.parseString(getShopAndSiteResult);
        if (shopObject.getInteger("state") == 200) {
            JSONArray dataArray = shopObject.getJSONArray("data");
            List<ShopAndSiteResp> shopAndSiteResps = StringUtility.jsonToList(dataArray.toString(), ShopAndSiteResp.class);
            for (ShopAndSiteResp shopAndSiteResp:shopAndSiteResps){
                OmsAccountVO omsAccountVO =new OmsAccountVO();
                OmsSiteVO omsSiteVO=new OmsSiteVO();
                List<OmsSiteVO> omsSiteVOList =new ArrayList<>();
                omsAccountVO.accountId=shopAndSiteResp.sellerid;
                omsAccountVO.accountName=shopAndSiteResp.shop_system;
                omsSiteVO.siteId=shopAndSiteResp.site_code;
                omsSiteVO.siteName=shopAndSiteResp.site_name;
                omsSiteVOList.add(omsSiteVO);
                omsAccountVO.lstOmsSite=omsSiteVOList;
                omsAccountVoList.add(omsAccountVO);
            }
        }
        return omsAccountVoList;
    }

    public static void main(String[] args) {
        DataAuthorizationImpl dataAuthorizationImpl = new DataAuthorizationImpl();
        List<OmsPlatformVO> omsPlatformVOS=  dataAuthorizationImpl.getPlatformList("test3");
        for (OmsPlatformVO omsPlatformVO :omsPlatformVOS){
            System.out.println(omsPlatformVO.platformName);
        }
        System.out.println("============================");
        String platform = "eBay";
        List<OmsAccountVO> omsAccountVoList  = dataAuthorizationImpl.getShopList("test3",platform);
        for (OmsAccountVO omsAccountVO :omsAccountVoList){
            System.out.println(omsAccountVO.accountName);
        }
    }
}
