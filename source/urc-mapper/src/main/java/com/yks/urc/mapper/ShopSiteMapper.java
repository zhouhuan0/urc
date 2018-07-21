package com.yks.urc.mapper;


import com.yks.urc.entity.ShopSiteDO;
import com.yks.urc.vo.OmsShopVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ShopSiteMapper {

    int insertBatchShopSite(@Param("shopSiteDOList") List<ShopSiteDO> shopSiteDOList);

    int deleteShopSite();

    List<ShopSiteDO> selectShopSiteByPlatformId(@Param("platformId") String platformId);
    /**
     * 分页查询亚马逊账号
     * @param:
     * @return
     * @Author lwx
     * @Date 2018/7/21 10:33
     */
    List<String> listShopByPage(Map<String, Object> data);

    long listShopByPageCount(Map<String,Object> data);
}