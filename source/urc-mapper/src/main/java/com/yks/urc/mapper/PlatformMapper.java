package com.yks.urc.mapper;

import com.yks.urc.entity.PlatformDO;
import com.yks.urc.vo.OmsPlatShopSiteVO;

import java.util.List;

public interface PlatformMapper {

    int insertPlatform(PlatformDO record);

    int deletePlatform();

    List<PlatformDO> selectAll();

    List<OmsPlatShopSiteVO> getAllPlatformShopSite();
}