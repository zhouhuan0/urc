package com.yks.urc.sellerid.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.sellerid.bp.api.ISysDataruleContext;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.OmsPlatformVO;
import com.yks.urc.vo.PlatformAccount4Third;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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
        handleIfAll(operValuesArr);
        return operValuesArr;
    }

    public void handleIfAll(List<String> operValuesArr) {
        if (CollectionUtils.isEmpty(operValuesArr)) {
            return;
        }

        for (int i = 0; i < operValuesArr.size(); i++) {
            OmsPlatformVO platformVO = serializeBp.json2ObjNew(operValuesArr.get(i), new TypeReference<OmsPlatformVO>() {
            });
            if (platformVO == null) {
                continue;
            }

            if (Boolean.TRUE.equals(platformVO.isAll)) {
                // 有所有账号权限时，可以清空账号数组
                platformVO.lstShop = Collections.EMPTY_LIST;
                operValuesArr.set(i, serializeBp.obj2JsonNonEmpty(platformVO));
            }
        }
    }

    public static List<String> getOperValuesArr(DataRuleSysVO sysVO) {
        if (sysVO == null || sysVO.row == null || CollectionUtils.isEmpty(sysVO.row.getSubWhereClause())) {
            return null;
        }
        return sysVO.row.getSubWhereClause().get(0).getOperValuesArr();
    }

    @Override
    public void handleIfAll(DataRuleSysVO sysVO) {
        handleIfAll(getOperValuesArr(sysVO));
    }

    public static void main(String[] args) {
        Boolean bln = null;
        System.out.println(Boolean.TRUE.equals(bln));
        bln = Boolean.FALSE;
        System.out.println(Boolean.TRUE.equals(bln));
        System.out.println(Boolean.TRUE.equals(bln));
        System.out.println(Boolean.TRUE.equals(true));
        System.out.println(Boolean.TRUE.equals(false));
    }
}
