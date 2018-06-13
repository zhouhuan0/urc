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
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.DataAuthorizationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class DataAuthorization {
    private static Logger logger = LoggerFactory.getLogger(DataAuthorization.class);
    /**
     * 请求平台url
     */
    @Value("${dataAuthorization.getPlatform}")
    private static final String GET_PLATFORM = "http://accounts.kokoerp.com/index.php?c=api-usercenter&a=getPlatformList";
    /**
     * 请求店铺账号和站点信息
     */
    @Value("${dataAuthorization.getShopAndSite}")
    private static final String GET_SHOP_AND_SITE = "http://accounts.kokoerp.com/index.php?c=api-usercenter&a=getShopList";

    /**
     * 获取平台信息
     *
     * @param
     * @return List<DataAuthorizationVo>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 20:52
     */
    public List<DataAuthorizationVo> getPlatform() {
        List<DataAuthorizationVo> authorList = null;
        String getPlatformResult = HttpUtility.httpGet(GET_PLATFORM);
        //将拿到的结果转为json ,获取平台信息
        JSONObject platformObject = StringUtility.parseString(getPlatformResult);
        if (platformObject.getInteger("state") == 200) {
            JSONArray dataArray = platformObject.getJSONArray("data");
            authorList = StringUtility.jsonToList(dataArray.toString(), DataAuthorizationVo.class);
            for (DataAuthorizationVo dataAuthorizationVo : authorList) {
                logger.info(dataAuthorizationVo.name);
            }
            //将平台传入到获取店铺账号和站点

        }
        return authorList;
    }

    /**
     * 获取平台下的站点和店铺信息
     *
     * @param authorizationVo
     * @return List<DataAuthorizationVo>
     * @Author linwanxian@youkeshu.com
     * @Date 2018/6/12 21:14
     */
    public List<DataAuthorizationVo> getShopAndSite(DataAuthorizationVo authorizationVo) {
        List<DataAuthorizationVo> authorList = null;
        String url = GET_SHOP_AND_SITE + "&platform=" + authorizationVo.platform;
        String getShopAndSiteResult = HttpUtility.httpGet(url);
        System.out.println(getShopAndSiteResult);
        JSONObject shopObject = StringUtility.parseString(getShopAndSiteResult);
        if (shopObject.getInteger("state") == 200) {
            JSONArray dataArray = shopObject.getJSONArray("data");
            authorList = StringUtility.jsonToList(dataArray.toString(), DataAuthorizationVo.class);
            for (DataAuthorizationVo authorizationVo1 : authorList) {
                logger.info(authorizationVo1.site_name + "," + authorizationVo1.shop_system);
            }
        }
        return authorList;
    }

    public static void main(String[] args) {
        DataAuthorization dataAuthorization = new DataAuthorization();
        dataAuthorization.getPlatform();
        DataAuthorizationVo vo = new DataAuthorizationVo();
        vo.platform = "亚马逊";
        dataAuthorization.getShopAndSite(vo);
    }
}
