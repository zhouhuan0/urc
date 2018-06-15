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

import com.yks.urc.vo.OmsAccountVO;
import com.yks.urc.vo.OmsPlatformVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DataAuthorizationImpl implements DataAuthorization {
    private static Logger logger = LoggerFactory.getLogger(DataAuthorizationImpl.class);
    /**
     * 请求平台url
     */
    @Value("${dataAuthorization.getPlatformList}")
    private static  String GET_PLATFORM = "http://accounts.kokoerp.com/index.php?c=api-usercenter&a=getPlatformList";
    /**
     * 请求店铺账号和站点信息
     */
    @Value("${dataAuthorization.getShopList}")
    private static  String GET_SHOP_AND_SITE = "http://accounts.kokoerp.com/index.php?c=api-usercenter&a=getShopList";

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
        List<OmsPlatformVO> omsPlatformVoList = null;
        String getPlatformResult = HttpUtility.httpGet(GET_PLATFORM);
        //将拿到的结果转为json ,获取平台信息
        JSONObject platformObject = StringUtility.parseString(getPlatformResult);
        if (platformObject.getInteger("state") == 200) {
            JSONArray dataArray = platformObject.getJSONArray("data");
            omsPlatformVoList = StringUtility.jsonToList(dataArray.toString(), OmsPlatformVO.class);
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
        List<OmsAccountVO> omsAccountVoList = null;
        String url = GET_SHOP_AND_SITE + "&platform=" + platform;
        String getShopAndSiteResult = HttpUtility.httpGet(url);
        JSONObject shopObject = StringUtility.parseString(getShopAndSiteResult);
        if (shopObject.getInteger("state") == 200) {
            JSONArray dataArray = shopObject.getJSONArray("data");
            omsAccountVoList = StringUtility.jsonToList(dataArray.toString(), OmsAccountVO.class);
        }
        return omsAccountVoList;
    }

    public static void main(String[] args) {
        DataAuthorizationImpl dataAuthorizationImpl = new DataAuthorizationImpl();
        dataAuthorizationImpl.getPlatformList(null);
        System.out.println("============================");
        String platform = "eBay";
        dataAuthorizationImpl.getShopList(null,platform);
    }
}
