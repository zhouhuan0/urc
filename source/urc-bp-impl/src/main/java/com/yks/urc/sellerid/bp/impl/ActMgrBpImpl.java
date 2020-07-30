package com.yks.urc.sellerid.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.actmgr.motan.service.api.IActMgrService;
import com.yks.pls.task.quatz.ITaskProvider;
import com.yks.urc.cache.bp.api.ILocalCacheBp;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.entity.ActmgrUserAccountRefVO;
import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.entity.DataRuleSysDO;
import com.yks.urc.fw.BeanProvider;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IActmgrUserAccountRefMapper;
import com.yks.urc.mapper.IDataRuleMapper;
import com.yks.urc.mapper.IDataRuleSysMapper;
import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.sellerid.bp.api.ISysDataruleContext;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ActMgrBpImpl implements IActMgrBp {

    @Autowired(required = false)
    private IActMgrService actMgrService;

    @Autowired
    private IConfigBp configBp;

    @Autowired
    private ISessionBp sessionBp;

    @Autowired
    private ISerializeBp serializeBp;

    @Override
    public void doAccountSyncTask(String param) throws Exception {
        TaskParamVO paramVO = serializeBp.json2ObjNew(param, new TypeReference<TaskParamVO>() {
        });
        if (paramVO == null) {
            paramVO = new TaskParamVO();
        }
        if (paramVO.minutes == null) {
            paramVO.minutes = 120;
        }

        Request4GetAccountInfo req = new Request4GetAccountInfo();
        String pointKey = "sycActMgr.lastPoint";
        req.setModifyDateStart(configBp.getStringFromDb(pointKey, "2020-05-01 00:00:00"));
        req.setModifyDateEnd(StringUtility.getDateTime_yyyyMMddHHmmssSSS(new Date(Math.min(
                new Date().getTime(),
                StringUtility.convertToDate(req.getModifyDateStart(), null).getTime() + paramVO.minutes * 60 * 1000L))));

        syncAct(req.getModifyDateStart(), req.getModifyDateEnd());

        // 更新lastPoint
        configBp.update2Db(pointKey, req.getModifyDateEnd());
    }

    @Autowired
    private IDataRuleService dataRuleService;

    public List<String> getSysKey() {
        return serializeBp.json2ObjNew(configBp.getString("actMgr.sysKey", "[\"001\",\"008\"]"), new TypeReference<List<String>>() {
        });
    }


    private List<String> getPlatCode() {
        return serializeBp.json2ObjNew(configBp.getString("actMgr.platCode", "[\"SE\"]"), new TypeReference<List<String>>() {
        });
    }

    private UserNameConfigVO getUserNameConfigVO() {
        return serializeBp.json2ObjNew(configBp.getString("actMgr.userNameList", "{ \"ifAll\":true}"), new TypeReference<UserNameConfigVO>() {
        });
    }

    private boolean ifUserNameInList(String userName) {
        UserNameConfigVO configVO = getUserNameConfigVO();
        if (configVO == null) {
            return false;
        }
        if (Boolean.TRUE.equals(configVO.ifAll)) {
            return true;
        }
        if (CollectionUtils.isEmpty(configVO.lstUserName)) {
            return false;
        }
        // 不在清单中的用户不使用新账号数据权限
        return configVO.lstUserName.contains(userName);
    }

    public void syncAct(String dtModifyStart, String dtModifyEnd) throws Exception {
        Request4GetUserAccountInfo req = new Request4GetUserAccountInfo();
        req.setModifyDateStart(dtModifyStart);
        req.setModifyDateEnd(dtModifyEnd);
        req.setPageNo(1);
        req.setPageSize(500);
        ResultVO<Response4GetUserAccountInfo> resp = getAccountInfoWithLog(req);
        if (!VoHelper.ifSuccess(resp)) {
            throw new Exception(serializeBp.obj2JsonNonEmpty(req));
        }
        if (resp.data == null) {
            return;
        }

        List<UserInfo4Third> lstAct = resp.data.getList();
        // 入库
        if (!CollectionUtils.isEmpty(lstAct)) {
            BeanProvider.getBean(IActMgrBp.class).saveAct(lstAct);
            // 发MQ
            sendMq(lstAct);
        }

        int totalPage = (int) (resp.data.getTotal() % req.getPageSize() > 0 ? resp.data.getTotal() / req.getPageSize() + 1 : resp.data.getTotal() / req.getPageSize());
        while (req.getPageNo() < totalPage) {
            // 下一页
            req.setPageNo(req.getPageNo() + 1);
            resp = getAccountInfoWithLog(req);
            lstAct = resp.data.getList();
            // 入库
            if (!CollectionUtils.isEmpty(lstAct)) {
                BeanProvider.getBean(IActMgrBp.class).saveAct(lstAct);
                // 发MQ
                sendMq(lstAct);
            }
        }
    }

    private void sendMq(List<UserInfo4Third> lstAct) {
        List<String> lstSysKey = getSysKey();
        if (CollectionUtils.isEmpty(lstAct) || CollectionUtils.isEmpty(lstSysKey)) {
            return;
        }
        lstAct.forEach(a -> {
            try {
                lstSysKey.forEach(s -> {
                    try {
                        dataRuleService.sendMq(a.getUserName(), s);
                    } catch (Exception e) {
                        taskProvider.writeErrorLog(e);
                    }
                });
            } catch (Exception ex) {
                taskProvider.writeErrorLog(ex);
            }
        });
    }

    private ISysDataruleContext getSysDataruleContext(String sysKey) {
        Map<String, ISysDataruleContext> map = BeanProvider.getBeansOfType(ISysDataruleContext.class);
        Iterator<Map.Entry<String, ISysDataruleContext>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            ISysDataruleContext rslt = it.next().getValue();
            if (StringUtility.stringEqualsIgnoreCase(rslt.getSysKey(), sysKey)) {
                return rslt;
            }
        }
        return null;
    }

    public void mergeAct(DataRuleSysVO sysDO) {
        if (sysDO == null) {
            return;
        }

        List<String> lstPlatCode = getPlatCode();
        List<String> lstOldPlatCode = getOldPlatCode(lstPlatCode);
        // 刊登和oms数据权限一致
        if (getSysKey().contains(sysDO.sysKey)) {
            if (sysDO.row == null) {
                sysDO.row = new ExpressionVO();
            }
            ISysDataruleContext ctx = getSysDataruleContext(sysDO.sysKey);

            sysDO.row.setIsAnd(1);
            if (CollectionUtils.isEmpty(sysDO.row.getSubWhereClause())) {
                sysDO.row.setSubWhereClause(new ArrayList<>());
                ExpressionVO e = new ExpressionVO();
                e.setOperValuesArr(null);
                e.setEntityCode(ctx.getEntityCode());
                e.setFieldCode(ctx.getFieldCode());
                sysDO.row.getSubWhereClause().add(e);
            }

            ExpressionVO oms = null;
            Optional<ExpressionVO> op = sysDO.row.getSubWhereClause().stream().filter(w -> w.getEntityCode().equalsIgnoreCase(ctx.getEntityCode())).findFirst();
            if (!op.isPresent()) {
                ExpressionVO e = new ExpressionVO();
                e.setOperValuesArr(null);
                e.setEntityCode(ctx.getEntityCode());
                e.setFieldCode(ctx.getFieldCode());
                sysDO.row.getSubWhereClause().add(e);
                oms = e;
            } else {
                oms = op.get();
            }
            List<String> lstPlat = oms.getOperValuesArr();
            if (CollectionUtils.isEmpty(lstPlat)) {
                lstPlat = new ArrayList<>();
                oms.setOperValuesArr(lstPlat);
            }
            for (int i = 0; i < lstPlat.size(); i++) {
                String mem = lstPlat.get(i);
                OmsPlatformVO platformVO = serializeBp.json2ObjNew(mem, new TypeReference<OmsPlatformVO>() {
                });
                // 只处理SE平台
                // 删除SE平台的旧数据权限
                if (platformVO != null && (lstPlatCode.contains(platformVO.platformId) ||
                        lstOldPlatCode.contains(platformVO.platformId))) {
                    lstPlat.remove(i);
                    i--;
                }
            }

            // 用账号管理系统的替换
            String json = userAccountRefMapper.getByUserNameAndEntityCode(sysDO.userName, ctx.getQueryEntityCode());
            if (StringUtils.isBlank(json)) {
                // 无账号管理系统权限,continue
                return;
            }
            UserInfo4Third u = serializeBp.json2ObjNew(json, new TypeReference<UserInfo4Third>() {
            });
            if (u == null) {
                return;
            }

            List<PlatformAccount4Third> lst = u.getPlatformAccount4ThirdList().stream().filter(c -> lstPlatCode.contains(c.getPlatformCode())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(lst)) {
                return;
            }
            for (PlatformAccount4Third t : lst) {
                if (t.getAccountList() == null) {
                    t.setAccountList(new ArrayList<>());
                }
                OmsPlatformVO actPlat = new OmsPlatformVO();
                actPlat.platformId = ctx.getPlatformId(t);
                actPlat.platCode = t.getPlatformCode();
                actPlat.isAll = StringUtility.stringEqualsIgnoreCaseObj(t.getIfAll(), 1);
                actPlat.platformName = t.getPlatformCode();
                if (!actPlat.isAll) {
                    actPlat.lstShop = t.getAccountList().stream().map(mem -> {
                        OmsShopVO shopVO = new OmsShopVO();
                        shopVO.shopId = mem.getAccountName();
                        shopVO.shopName = mem.getAccountName();
                        shopVO.accountId = mem.getAccountId();
                        return shopVO;
                    }).collect(Collectors.toList());
                }
                lstPlat.add(serializeBp.obj2JsonNonEmpty(actPlat));
            }
        }
    }

    private static Map<String, String> mapNew2Old = new HashMap<>();

    static {
        mapNew2Old.put("SE", "SHOPEE");
    }

    private List<String> getOldPlatCode(List<String> lstPlatCode) {
        List<BasePlatformInfo> lst = getPlatMapFromLocalCache();
        if (CollectionUtils.isEmpty(lst)) {
            lst = getPlatMapFromActMgr();
            if (!CollectionUtils.isEmpty(lst)) {
                // 入缓存,缓存6小时
                localCacheBp.setLocalCache(platCodeMapCacheName, lst, 1000 * 60 * 60 * 6L);
            }
        }

        if (!CollectionUtils.isEmpty(lst)) {
            return lst.stream().filter(p -> lstPlatCode.contains(p.getPlatformCode())).map(p -> p.getPlatformCodeOld()).collect(Collectors.toList());
        }
        return lstPlatCode.stream().map(c -> mapNew2Old.get(c)).collect(Collectors.toList());
    }

    @Autowired
    private ILocalCacheBp localCacheBp;

    private String platCodeMapCacheName = "platCodeMap";

    private List<BasePlatformInfo> getPlatMapFromLocalCache() {
        List<BasePlatformInfo> lst = localCacheBp.getLocalCache(platCodeMapCacheName, new TypeReference<List<BasePlatformInfo>>() {
        });
        return lst;
    }

    private List<BasePlatformInfo> getPlatMapFromActMgr() {
        ResultVO<List<BasePlatformInfo>> rslt = actMgrService.getPlatformCode("{}");
        if (VoHelper.ifSuccess(rslt)) {
            return rslt.data;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void mergeAct(List<DataRuleSysVO> lstDr) {
        if (CollectionUtils.isEmpty(lstDr)) {
            return;
        }
        // 入参为老数据权限，将账号管理系统的账号权限替换到老数据权限中
        for (DataRuleSysVO sysDO : lstDr) {
            if (ifUserNameInList(sysDO.userName)) {
                // 在清单中的用户才使用新账号数据权限
                mergeAct(sysDO);
            }
        }
    }

    @Autowired
    private ITaskProvider taskProvider;

    private ResultVO<Response4GetUserAccountInfo> getAccountInfoWithLog(Request4GetUserAccountInfo req) {
        ResultVO<Response4GetUserAccountInfo> resp = actMgrService.getUserAccountInfo(serializeBp.obj2JsonNonEmpty(req));
        taskProvider.writeInfoLog(String.format("getUserAccountInfo_%s_%s", req.getModifyDateStart(),
                req.getModifyDateEnd()), String.format("%s\r\n%s", serializeBp.obj2JsonNonEmpty(req), serializeBp.obj2JsonNonEmpty(resp)));
        return resp;
    }

    @Autowired
    private IActmgrUserAccountRefMapper userAccountRefMapper;

    @Autowired
    private IDataRuleMapper dataRuleMapper;

    @Autowired
    private IDataRuleSysMapper dataRuleSysMapper;

    @Autowired
    private ISeqBp seqBp;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAct(List<UserInfo4Third> lstAct) {
        if (CollectionUtils.isEmpty(lstAct)) {
            return;
        }
        // insert or update actmgr_user_account_ref
        List<ActmgrUserAccountRefVO> lstRef = new ArrayList<>();

        for (UserInfo4Third u : lstAct) {
            ActmgrUserAccountRefVO refVO = new ActmgrUserAccountRefVO();
            refVO.setUserName(u.getUserName());
            refVO.setEntityCode(StringConstant.E_PlatformShopSite);
            refVO.setActJson(serializeBp.obj2JsonNonEmpty(u));
            refVO.setCreater(sessionBp.getOperator());
            refVO.setModifier(sessionBp.getOperator());
            lstRef.add(refVO);
        }
        userAccountRefMapper.insertOrUpdate(lstRef);
        lstAct.forEach(u -> saveOneUser(u));
    }

    private void saveOneUser(UserInfo4Third u) {
        // 处理 urc_data_rule 表
        String userName = u.getUserName();
        List<DataRuleDO> lstDr = dataRuleMapper.getDataRuleByUserName(Arrays.asList(userName));
        Long dataRuleId = null;
        if (CollectionUtils.isEmpty(lstDr)) {
            // insert urc_data_rule
            DataRuleDO dr = new DataRuleDO();
            dr.setUserName(userName);
            dataRuleId = seqBp.getNextDataRuleId();
            dr.setDataRuleId(dataRuleId);
            dr.setCreateBy(sessionBp.getOperator());
            dataRuleMapper.insertBatch(Arrays.asList(dr));
        } else {
            dataRuleId = lstDr.get(0).getDataRuleId();
        }
        // 处理 urc_data_rule_sys 表
        List<String> lstSysKey = Arrays.asList("001", "008");
        for (String sysKey : lstSysKey) {
            DataRuleSysDO drs = dataRuleSysMapper.getDataRuleSysBy(userName, sysKey);
            if (drs == null) {
                // insert urc_data_rule_sys
                drs = new DataRuleSysDO();
                drs.setDataRuleId(dataRuleId);
                Long dataRuleSysId = seqBp.getNextDataRuleSysId();
                drs.setDataRuleSysId(dataRuleSysId);
                drs.setSysKey(sysKey);
                drs.setCreateBy(sessionBp.getOperator());
                drs.setModifiedBy(sessionBp.getOperator());
                dataRuleSysMapper.insert(drs);
            } else {
                // 更新 urc_data_rule_sys 表的的创建时间，com.yks.urc.motan.service.impl.UrcMgrImpl.getDataRuleGtDt 才能取到更新的数据
                dataRuleSysMapper.updateModifiedTime(drs.getDataRuleSysId());
            }
        }
    }
}
