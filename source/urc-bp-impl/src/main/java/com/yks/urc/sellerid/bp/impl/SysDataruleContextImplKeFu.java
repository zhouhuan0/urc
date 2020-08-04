package com.yks.urc.sellerid.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.sellerid.bp.api.ISysDataruleContext;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.OmsPlatformVO;
import com.yks.urc.vo.PlatformAccount4Third;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class SysDataruleContextImplKeFu implements ISysDataruleContext {
    @Override
    public String getSysKey() {
        return "009";
    }


    @Override
    public String getEntityCode() {
        return StringConstant.E_CustomerService;
    }

    @Override
    public String getFieldCode() {
        return StringConstant.F_CustomerService;
    }

    @Override
    public String getQueryEntityCode() {
        return StringConstant.E_CustomerService;
    }

    @Override
    public String getPlatformId(PlatformAccount4Third t) {
        return t.getPlatformCodeOld();
    }

    @Autowired
    private IActMgrBp actMgrBp;

    @Autowired
    private ISerializeBp serializeBp;

    @Autowired
    private SysDataruleContextImplPls sysDataruleContextImplPls;

    @Override
    public List<String> filterActMgrPlatCode(List<String> operValuesArr) {
        return sysDataruleContextImplPls.filterActMgrPlatCode(operValuesArr);
    }

    @Override
    public void handleIfAll(DataRuleSysVO sysVO) {
        sysDataruleContextImplPls.handleIfAll(sysVO);
    }

    @Override
    public String getLastPointKey() {
        return SysDataruleContextImplPls.getLastPointKey(getSysKey());
    }

    @Override
    public List<Integer> getRoleIds() {
//        0    账号管理员
//        10    销售总监
//        11    销售经理
//        12    销售主管
//
//
//        20    客服总监
//        21    客服经理
//        22    客服主管
//        23    客服专员
//
//        101    财务专员
//
//        107    资产专员
//        108    特殊总监
        return Arrays.asList(0,
                10,
                11,
                12,
                20,
                21,
                22,
                23,
                101,
                107,
                108);
    }

    @Autowired
    private IMqBp mqBp;

    @Override
    public List<String> getSendMqSysKey() {
        // 客服已使用pull方式,不用发消息了
        if (mqBp.getNotSendMqSysKey().contains(getSysKey())) {
            return null;
        }
        return Arrays.asList(getSysKey());
    }
}
