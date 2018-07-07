package com.yks.urc.mapper;


import com.yks.urc.entity.ShopSiteDO;
import com.yks.urc.vo.OmsShopVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopSiteMapper {

    int insertBatchShopSite(@Param("shopSiteDOList") List<ShopSiteDO> shopSiteDOList);

    int deleteShopSite();

    List<ShopSiteDO> selectShopSiteByPlatformId(@Param("platformId") String platformId);
}