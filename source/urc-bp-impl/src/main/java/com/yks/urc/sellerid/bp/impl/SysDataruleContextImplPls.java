package com.yks.urc.sellerid.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.sellerid.bp.api.ISysDataruleContext;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.vo.OmsPlatformVO;
import com.yks.urc.vo.PlatformAccount4Third;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class SysDataruleContextImplPls implements ISysDataruleContext {
    @Override
    public String getSysKey() {
        return "008";
    }


    @Override
    public String getEntityCode() {
        return StringConstant.E_PlsShopAccount;
    }

    @Override
    public String getFieldCode() {
        return StringConstant.F_PlsShopAccount;
    }

    @Override
    public String getQueryEntityCode() {
        return StringConstant.E_PlatformShopSite;
    }

    @Override
    public String getPlatformId(PlatformAccount4Third t) {
        return t.getPlatformCodeOld();
    }

    @Autowired
    private IActMgrBp actMgrBp;

    @Autowired
    private ISerializeBp serializeBp;

    @Override
    public List<String> filterActMgrPlatCode(List<String> operValuesArr) {
        if (CollectionUtils.isEmpty(operValuesArr)) {
            return operValuesArr;
        }

        for (int i = 0; i < operValuesArr.size(); i++) {
            OmsPlatformVO platformVO = serializeBp.json2ObjNew(operValuesArr.get(i), new TypeReference<OmsPlatformVO>() {
            });
            if (platformVO == null) {
                continue;
            }

            if (actMgrBp.getPlatCode().contains(actMgrBp.getNewPlatCode(platformVO.platformId))) {
                operValuesArr.remove(i);
                i--;
            }
        }
        return operValuesArr;
    }
}
