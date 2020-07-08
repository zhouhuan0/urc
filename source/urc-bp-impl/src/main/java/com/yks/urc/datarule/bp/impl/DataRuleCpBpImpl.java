package com.yks.urc.datarule.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.datarule.bp.api.IDataRuleCpBp;
import com.yks.urc.entity.DataRuleSysDO;
import com.yks.urc.entity.ExpressionDO;
import com.yks.urc.mapper.IDataRuleMapper;
import com.yks.urc.mapper.IDataRuleSysMapper;
import com.yks.urc.mapper.IExpressionMapper;
import com.yks.urc.mapper.IUserMapper;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class DataRuleCpBpImpl implements IDataRuleCpBp {

    @Autowired
    IMqBp mqBp;

    @Autowired
    private IDataRuleService dataRuleService;

    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private ISeqBp seqBp;

    @Autowired
    private IExpressionMapper expressionMapper;

    @Autowired
    private IDataRuleSysMapper dataRuleSysMapper;

    @Autowired
    private IDataRuleMapper dataRuleMapper;

    @Autowired
    private ISerializeBp serializeBp;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cpOms2Pls(String userName) {
        // 查找oms shopify数据权限，赋给刊登
        String operator = "panyun";

        ResultVO<List<DataRuleVO>> rslt = dataRuleService.getDataRuleByUser(Arrays.asList(userName), "panyun", "001");
        if (CollectionUtils.isEmpty(rslt.data)) {
            return;
        }
        List<DataRuleSysVO> lstDataRuleSys = rslt.data.get(0).lstDataRuleSys;
        Optional<DataRuleSysVO> op = lstDataRuleSys.stream().filter(c -> c.sysKey.equalsIgnoreCase("001")).findFirst();
        if (!op.isPresent()) {
            // 没有oms数据权限，不用处理
            return;
        }
        DataRuleSysVO dr = op.get();
        Optional<ExpressionVO> exp = dr.row.getSubWhereClause().stream().filter(c -> c.getEntityCode().equalsIgnoreCase("E_PlatformShopSite")).findFirst();
        if (!exp.isPresent()) {
            return;
        }
        ExpressionVO e = exp.get();
        if (CollectionUtils.isEmpty(e.getOperValuesArr())) {
            return;
        }
        for (String mem : e.getOperValuesArr()) {
            OmsPlatformVO omsPlatformVO = serializeBp.json2ObjNew(mem, new TypeReference<OmsPlatformVO>() {
            });
            if (omsPlatformVO.platformId.equalsIgnoreCase("SF")) {
                // 此处表示oms中有sf数据权限
                OmsPlatformVO plsPlatformVO = serializeBp.json2ObjNew(serializeBp.obj2Json(omsPlatformVO), new TypeReference<OmsPlatformVO>() {
                });
                plsPlatformVO.platformId = "shopify";
                plsPlatformVO.platformName = plsPlatformVO.platformId;

                // 查找刊登的数据权限
                Optional<DataRuleSysVO> opPls = lstDataRuleSys.stream().filter(c -> c.sysKey.equalsIgnoreCase("008")).findFirst();
                DataRuleSysVO plsDr = null;
                if (opPls.isPresent()) {
                    plsDr = opPls.get();
                    for (ExpressionVO w : plsDr.row.getSubWhereClause()) {
                        // 将 shopify 的替换掉
                        String sf = null;
                        if (w.getOperValuesArr() == null) {
                            w.setOperValuesArr(new ArrayList<>());
                        }

                        for (String s : w.getOperValuesArr()) {
                            OmsPlatformVO cur = serializeBp.json2ObjNew(s, new TypeReference<OmsPlatformVO>() {
                            });
                            if (cur.platformId.equalsIgnoreCase("shopify")) {
                                sf = s;
                            }
                        }

                        if (sf != null) {
                            w.getOperValuesArr().remove(sf);
                        }
                        if (plsPlatformVO.isAll == null) {
                            plsPlatformVO.isAll = false;
                        }
                        w.getOperValuesArr().add(serializeBp.obj2Json(plsPlatformVO));
                    }
                    // 存在刊登的数据权限时，只需要更新 子级 expression 记录即可
                    ExpressionVO expSub = plsDr.row.getSubWhereClause().get(0);
                    ExpressionDO expUpdate = new ExpressionDO();
                    expUpdate.setExpressionId(expSub.getExpressionId());
                    expUpdate.setOperValues(serializeBp.obj2Json(expSub.getOperValuesArr()));
                    expUpdate.setModifiedBy(operator);
                    expressionMapper.updateOperValuesByExpressionId(expUpdate);

                    // 更新 urc_data_rule_sys 表的创建时间和最后修改时间
                    DataRuleSysDO dataRUleSysDO = dataRuleSysMapper.getDataRuleSysBy(userName, "008");
                    dataRuleSysMapper.updateModifiedTime(dataRUleSysDO.getDataRuleSysId());
                } else {
                    plsDr = new DataRuleSysVO();
                    plsDr.sysKey = "008";
                    plsDr.userName = userName;
                    plsDr.row = new ExpressionVO();
                    ExpressionVO subWhere = new ExpressionVO();
                    subWhere.setOperValuesArr(Arrays.asList(serializeBp.obj2Json(plsPlatformVO)));
                    subWhere.setEntityCode("E_PlsShopAccount");
                    subWhere.setFieldCode("F_PlsShopAccount");
                    plsDr.row.setSubWhereClause(Arrays.asList(subWhere));

                    // insert urc_data_rule_sys 表
                    DataRuleSysDO dataRuleSys = new DataRuleSysDO();
                    Long dataRuleSysId = seqBp.getNextDataRuleSysId();
                    dataRuleSys.setDataRuleSysId(dataRuleSysId);
                    dataRuleSys.setDataRuleId(dataRuleMapper.getDataRuleByUserName(Arrays.asList(userName)).get(0).getDataRuleId());
                    dataRuleSys.setSysKey("008");
                    dataRuleSys.setCreateTime(new Date());
                    dataRuleSys.setCreateBy(operator);
                    dataRuleSys.setModifiedBy(operator);
                    dataRuleSysMapper.insertBatch(Arrays.asList(dataRuleSys));


                    List<ExpressionDO> expressionCache = new ArrayList<>();
                    /*组装父级行权限*/
                    ExpressionDO parentExpressionDO = new ExpressionDO();
                    Long parentExpressionId = seqBp.getExpressionId();
                    parentExpressionDO.setExpressionId(parentExpressionId);
                    parentExpressionDO.setDataRuleSysId(dataRuleSysId);
                    parentExpressionDO.setFieldCode(null);
                    parentExpressionDO.setParentExpressionId(null);
                    parentExpressionDO.setAnd(true);
                    parentExpressionDO.setCreateBy(operator);
                    parentExpressionDO.setCreateTime(new Date());
                    expressionCache.add(parentExpressionDO);

                    /*组装子级行权限数据*/
                    ExpressionDO subExpressionDO = new ExpressionDO();
                    Long expressionId = seqBp.getExpressionId();
                    subExpressionDO.setExpressionId(expressionId);
                    subExpressionDO.setDataRuleSysId(dataRuleSysId);
                    subExpressionDO.setEntityCode(subWhere.getEntityCode());
                    subExpressionDO.setFieldCode(subWhere.getFieldCode());
                    subExpressionDO.setOperValues(serializeBp.obj2Json(subWhere.getOperValuesArr()));
                    subExpressionDO.setParentExpressionId(parentExpressionId);
                    subExpressionDO.setCreateTime(new Date());
                    subExpressionDO.setCreateBy(operator);
                    expressionCache.add(subExpressionDO);
                    expressionMapper.insertBatch(expressionCache);
                }

                DataRuleVO mqVO = new DataRuleVO();
                mqVO.userName = userName;
                mqVO.lstDataRuleSys = new ArrayList<>();
                plsDr.col = null;
                plsDr.sysName = "刊登";
                plsDr.t = String.valueOf(new Date().getTime());
                plsDr.userName = userName;
                mqVO.lstDataRuleSys.add(plsDr);
                mqBp.send2Mq(mqVO);
            }
        }
    }
}
