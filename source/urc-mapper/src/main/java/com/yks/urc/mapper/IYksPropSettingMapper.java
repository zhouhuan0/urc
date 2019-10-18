package com.yks.urc.mapper;

import com.yks.urc.entity.YksPropSetting;

import java.util.List;

public interface IYksPropSettingMapper {

    List<YksPropSetting> selectOmsPropSettingObjList(YksPropSetting obj);

    void insertOrUpdateOmsProSeting(YksPropSetting enti);

    YksPropSetting selectOmsProSetingByProoKey(String propKey);


}
