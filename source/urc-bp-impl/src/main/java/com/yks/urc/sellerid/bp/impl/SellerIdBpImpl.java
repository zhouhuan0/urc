package com.yks.urc.sellerid.bp.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.sellerid.bp.api.ISellerIdBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.CheckSellerIdRespVO;
import com.yks.urc.vo.OmsPlatformVO;
import com.yks.urc.vo.OmsShopVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SellerIdBpImpl implements ISellerIdBp {
    @Autowired
    private IDataRuleService dataRuleService;

    @Override
    public ResultVO checkSellerId(String jsonStr) {
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);

            if(null == jsonObject){
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "参数异常");
            }
            String entityCode = jsonObject.getString("entityCode");
            String platformCode = jsonObject.getString("platformCode");
            String keys = jsonObject.getString("keys");
            List<String> lstSellerId = JSONArray.parseArray(jsonObject.getString("lstSellerId"), String.class);//要检测的销售账号【必填】for checkSellerId
            if (StringUtility.isNullOrEmpty(entityCode)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "entityCode为空");
            }
            if (StringUtility.isNullOrEmpty(platformCode)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "platformCode为空");
            }
            if (CollectionUtils.isEmpty(lstSellerId)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "lstSellerId为空");
            }
            String operator = MotanSession.getRequest().getOperator();
            jsonObject.put("operator", operator);

            ResultVO resultVOTemp =  dataRuleService.getPlatformShopByConditions(jsonObject);
            List<OmsPlatformVO> omsPlatformVOList = (List<OmsPlatformVO>) resultVOTemp.data;
            CheckSellerIdRespVO checkSellerIdRespVO = new CheckSellerIdRespVO();
            Set<String> notOkSellerId = new HashSet<>();
            Set<String> okSellerId = new HashSet<>();
            Set<String> okSellerId4Input = new HashSet<>();
            if(CollectionUtils.isEmpty(omsPlatformVOList)){
                notOkSellerId.addAll(lstSellerId);
            }
            List<OmsShopVO> lstShop = omsPlatformVOList.get(0).lstShop;
            for (String sellerId : lstSellerId) {
                for (OmsShopVO omsShopVO : lstShop) {
                    if(null !=sellerId && sellerId.equalsIgnoreCase(omsShopVO.shopId)){
                        okSellerId.add(omsShopVO.shopId);
                        okSellerId4Input.add(sellerId);
                        break;
                    }
                }
            }
            lstSellerId.removeAll(new ArrayList<>(okSellerId4Input));
            checkSellerIdRespVO.setNotOkSellerId(lstSellerId);
            checkSellerIdRespVO.setOkSellerId(new ArrayList<>(okSellerId));
            return VoHelper.getSuccessResult(checkSellerIdRespVO);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"匹配正确的销售账号失败.");
        }
    }
}
