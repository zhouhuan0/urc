package com.yks.urc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class WsTest extends BaseServiceTest {
    @Autowired
    IUrcMgr urcMgr;

    @Test
    public void test1() {
        Map map = new HashMap();
        map.put("pageNumber", 1);
        map.put("pageData", 100);
        map.put("positionIds", "[1606805482420000065]");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getUserByPosition(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test2() {
        String json = "{\"data\":{\"positionId\":\"79\",\"isSupperAdmin\":1},\"ticket\":\"5adf338494b39326142c88e777cf2db5\",\"operator\":\"wensheng\",\"personName\":\"文胜\",\"funcVersion\":\"557a74e41f37da5c73a4193e7f008809\",\"moduleUrl\":\"/user/rolemanagement/\",\"requestId\":\"120815320501962f1fcd61d526a84c40\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.setSupperAdmin(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test3() {
        String json = "{\"data\":{\"groupId\":\"1607999783590000093\",\"groupName\":\"物流1\",\"positions\":[{\"isActive\":1,\"positionId\":\"2\",\"positionName\":\"PHP\"}],\"selectedContext\":[{\"sysKey\":\"002\",\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"002-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000001-000004-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000001-000004-003\\\",\\\"name\\\":\\\"订单申报\\\"},{\\\"key\\\":\\\"002-000001-000004-004\\\",\\\"name\\\":\\\"收款单申报\\\"},{\\\"key\\\":\\\"002-000001-000004-005\\\",\\\"name\\\":\\\"物流单申报\\\"},{\\\"key\\\":\\\"002-000001-000004-006\\\",\\\"name\\\":\\\"清单申报\\\"}],\\\"key\\\":\\\"002-000001-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"订单管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/riskmanagement\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000005-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000001-000005-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000001-000005-003\\\",\\\"name\\\":\\\"导入六联单及车牌信息\\\"},{\\\"key\\\":\\\"002-000001-000005-004\\\",\\\"name\\\":\\\"总分单申报\\\"},{\\\"key\\\":\\\"002-000001-000005-005\\\",\\\"name\\\":\\\"删除/确认完成\\\"}],\\\"key\\\":\\\"002-000001-000005\\\",\\\"module\\\":[],\\\"name\\\":\\\"六联单列表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/sixupList\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000001-000006-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000001-000006-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000001-000006-003\\\",\\\"name\\\":\\\"汇总申报\\\"}],\\\"key\\\":\\\"002-000001-000006\\\",\\\"module\\\":[],\\\"name\\\":\\\"汇总单列表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/declaration/summaryList\\\",\\\"menu\\\":[]}],\\\"name\\\":\\\"报关管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000002-000001-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000002-000001\\\",\\\"name\\\":\\\"轨迹查询\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/query/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000002-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000002-000002\\\",\\\"name\\\":\\\"渠道设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/channelset/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000002-000003-002\\\",\\\"name\\\":\\\"设置\\\"}],\\\"key\\\":\\\"002-000002-000003\\\",\\\"name\\\":\\\"轨迹状态设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/channelstateset/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000004-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000002-000004\\\",\\\"name\\\":\\\"预警设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/warningsetting/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000002-000005-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000002-000005\\\",\\\"name\\\":\\\"预警监控分析\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/trajectory/warningmonitoring/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"物流轨迹\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000003\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000001-002\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"002-000003-000001\\\",\\\"name\\\":\\\"物流服务商\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/provider/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000002-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000003-000002\\\",\\\"name\\\":\\\"面单管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/management/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000003-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000003-000003\\\",\\\"name\\\":\\\"渠道分区码管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/partition/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000004-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000003-000004-003\\\",\\\"name\\\":\\\"上传追踪码\\\"},{\\\"key\\\":\\\"002-000003-000004-004\\\",\\\"name\\\":\\\"修改\\\"},{\\\"key\\\":\\\"002-000003-000004-005\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"002-000003-000004\\\",\\\"name\\\":\\\"追踪码上传\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/trackingnumber/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000008-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000003-000008\\\",\\\"name\\\":\\\"预备追踪号上传\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/trackingupload/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000009-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000003-000009\\\",\\\"name\\\":\\\"SKU申报信息维护\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/declarationinfo/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000010-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000003-000010\\\",\\\"name\\\":\\\"申报价值配置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/valuesetting/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000005-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000005-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000003-000005\\\",\\\"name\\\":\\\"申报价值记录\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/valueRecord/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000006-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000003-000006-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000003-000006-003\\\",\\\"name\\\":\\\"导入\\\"},{\\\"key\\\":\\\"002-000003-000006-004\\\",\\\"name\\\":\\\"删除\\\"},{\\\"key\\\":\\\"002-000003-000006-005\\\",\\\"name\\\":\\\"修改\\\"}],\\\"key\\\":\\\"002-000003-000006\\\",\\\"name\\\":\\\"渠道有效期配置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/validDateConfig/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000003-000007-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000003-000007\\\",\\\"name\\\":\\\"订单日志查看\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/servicemanage/orderLogs/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"物流商管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000006\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000006-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000006-000001-002\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"002-000006-000001-003\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"002-000006-000001\\\",\\\"name\\\":\\\"渠道管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/headmanagement/channel/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000006-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000006-000002-002\\\",\\\"name\\\":\\\"新增主体\\\"},{\\\"key\\\":\\\"002-000006-000002-003\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"002-000006-000002\\\",\\\"name\\\":\\\"进出口商\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/headmanagement/company/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000006-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000006-000003-002\\\",\\\"name\\\":\\\"新增\\\"},{\\\"key\\\":\\\"002-000006-000003-003\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"002-000006-000003\\\",\\\"name\\\":\\\"目的国设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/headmanagement/country/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000006-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000006-000004-002\\\",\\\"name\\\":\\\"导入出库单\\\"},{\\\"key\\\":\\\"002-000006-000004-003\\\",\\\"name\\\":\\\"编辑\\\"},{\\\"key\\\":\\\"002-000006-000004-004\\\",\\\"name\\\":\\\"编辑-保存\\\"},{\\\"key\\\":\\\"002-000006-000004-005\\\",\\\"name\\\":\\\"出库单\\\"},{\\\"key\\\":\\\"002-000006-000004-006\\\",\\\"name\\\":\\\"出库单-导出\\\"},{\\\"key\\\":\\\"002-000006-000004-007\\\",\\\"name\\\":\\\"出库单-导入\\\"},{\\\"key\\\":\\\"002-000006-000004-008\\\",\\\"name\\\":\\\"出库单-编辑\\\"}],\\\"key\\\":\\\"002-000006-000004\\\",\\\"name\\\":\\\"在途明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/headmanagement/intransitdetail/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000006-000005-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000006-000005-002\\\",\\\"name\\\":\\\"数据导出\\\"},{\\\"key\\\":\\\"002-000006-000005-003\\\",\\\"name\\\":\\\"分摊详情\\\"},{\\\"key\\\":\\\"002-000006-000005-004\\\",\\\"name\\\":\\\"分摊详情-重新分摊\\\"},{\\\"key\\\":\\\"002-000006-000005-005\\\",\\\"name\\\":\\\"分摊详情-导入申报价值\\\"},{\\\"key\\\":\\\"002-000006-000005-006\\\",\\\"name\\\":\\\"分摊详情-导出\\\"}],\\\"key\\\":\\\"002-000006-000005\\\",\\\"name\\\":\\\"费用信息\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/headmanagement/feeinformation/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"头程管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000007\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000007-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000007-000001-002\\\",\\\"name\\\":\\\"导入订单\\\"},{\\\"key\\\":\\\"002-000007-000001-003\\\",\\\"name\\\":\\\"导出运单号\\\"}],\\\"key\\\":\\\"002-000007-000001\\\",\\\"name\\\":\\\"采购物流\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/procurementLogistics/procurementLogistics/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"采购物流\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000005\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000005-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000005-000001\\\",\\\"name\\\":\\\"导入导出队列\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/datamanagement/importexportqueue/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"数据管理\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000008\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000001-002\\\",\\\"name\\\":\\\"创建账单\\\"},{\\\"key\\\":\\\"002-000008-000001-003\\\",\\\"name\\\":\\\"增加费用\\\"},{\\\"key\\\":\\\"002-000008-000001-004\\\",\\\"name\\\":\\\"日志\\\"},{\\\"key\\\":\\\"002-000008-000001-005\\\",\\\"name\\\":\\\"核对\\\"},{\\\"key\\\":\\\"002-000008-000001-006\\\",\\\"name\\\":\\\"制单\\\"},{\\\"key\\\":\\\"002-000008-000001-007\\\",\\\"name\\\":\\\"提交审核\\\"},{\\\"key\\\":\\\"002-000008-000001-008\\\",\\\"name\\\":\\\"费用单\\\"},{\\\"key\\\":\\\"002-000008-000001-009\\\",\\\"name\\\":\\\"审核\\\"},{\\\"key\\\":\\\"002-000008-000001-010\\\",\\\"name\\\":\\\"审核记录\\\"},{\\\"key\\\":\\\"002-000008-000001-011\\\",\\\"name\\\":\\\"付款水单\\\"},{\\\"key\\\":\\\"002-000008-000001-012\\\",\\\"name\\\":\\\"子列表-明细\\\"},{\\\"key\\\":\\\"002-000008-000001-013\\\",\\\"name\\\":\\\"子列表-删除\\\"},{\\\"key\\\":\\\"002-000008-000001-014\\\",\\\"name\\\":\\\"付款水单-查看文件\\\"},{\\\"key\\\":\\\"002-000008-000001-015\\\",\\\"name\\\":\\\"付款水单-下载文件\\\"},{\\\"key\\\":\\\"002-000008-000001-016\\\",\\\"name\\\":\\\"付款水单-删除文件\\\"},{\\\"key\\\":\\\"002-000008-000001-017\\\",\\\"name\\\":\\\"付款水单-上传文件\\\"},{\\\"key\\\":\\\"002-000008-000001-018\\\",\\\"name\\\":\\\"删除\\\"}],\\\"key\\\":\\\"002-000008-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000001-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000001-000001-002\\\",\\\"name\\\":\\\"导出数据\\\"},{\\\"key\\\":\\\"002-000008-000001-000001-003\\\",\\\"name\\\":\\\"导入核算结果\\\"},{\\\"key\\\":\\\"002-000008-000001-000001-004\\\",\\\"name\\\":\\\"核算结果\\\"}],\\\"key\\\":\\\"002-000008-000001-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"核对运费\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/checkbill/checkfreight/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000001-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000001-000002-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000008-000001-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"基本运费\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/checkbill/basedetail/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000001-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000001-000003-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000008-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"其他费用\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/checkbill/otherdetail/\\\",\\\"menu\\\":[]},{\\\"function\\\":[],\\\"key\\\":\\\"002-000008-000001-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"制单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/checkbill/makebill/\\\"}],\\\"name\\\":\\\"核对账单\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/checkbill/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000002-002\\\",\\\"name\\\":\\\"物流商设置-新增\\\"},{\\\"key\\\":\\\"002-000008-000002-003\\\",\\\"name\\\":\\\"物流商设置-修改\\\"},{\\\"key\\\":\\\"002-000008-000002-004\\\",\\\"name\\\":\\\"费用信息管理-新增\\\"},{\\\"key\\\":\\\"002-000008-000002-005\\\",\\\"name\\\":\\\"费用信息管理-修改\\\"},{\\\"key\\\":\\\"002-000008-000002-006\\\",\\\"name\\\":\\\"费用信息管理-删除\\\"}],\\\"key\\\":\\\"002-000008-000002\\\",\\\"name\\\":\\\"对账设置\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/setting/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000008-000004-002\\\",\\\"name\\\":\\\"导出报表\\\"},{\\\"key\\\":\\\"002-000008-000004-003\\\",\\\"name\\\":\\\"导出明细\\\"}],\\\"key\\\":\\\"002-000008-000004\\\",\\\"name\\\":\\\"账单付款报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/accountPayment/\\\",\\\"menu\\\":[],\\\"module\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000008-000003-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000008-000003\\\",\\\"name\\\":\\\"导出物流信息\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/reconciliation/exportlogisticsinfo/\\\",\\\"menu\\\":[],\\\"module\\\":[]}],\\\"name\\\":\\\"物流对账\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]},{\\\"key\\\":\\\"002-000009\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000001-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000009-000001-003\\\",\\\"name\\\":\\\"查看30天趋势\\\"}],\\\"key\\\":\\\"002-000009-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"物流日发货报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/dailyDelivery/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000002-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000002-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000009-000002-003\\\",\\\"name\\\":\\\"明细导出\\\"},{\\\"key\\\":\\\"002-000009-000002-004\\\",\\\"name\\\":\\\"妥投数量导出\\\"}],\\\"key\\\":\\\"002-000009-000002\\\",\\\"module\\\":[],\\\"name\\\":\\\"物流妥投汇总报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/properInvestment/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000003-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000003-002\\\",\\\"name\\\":\\\"导出\\\"},{\\\"key\\\":\\\"002-000009-000003-003\\\",\\\"name\\\":\\\"查看明细\\\"},{\\\"key\\\":\\\"002-000009-000003-004\\\",\\\"name\\\":\\\"轨迹数据导出\\\"}],\\\"key\\\":\\\"002-000009-000003\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000003-000001-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000003-000001-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000009-000003-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"渠道日发货明细\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":0,\\\"url\\\":\\\"/lgtconfig/report/channelReport/detail/\\\",\\\"menu\\\":[]}],\\\"name\\\":\\\"物流渠道日报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/channelReport/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000004-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000009-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"渠道发货报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/channelDelivery/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000005-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000009-000005\\\",\\\"module\\\":[],\\\"name\\\":\\\"发货妥投报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/shipDeliveried/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000006-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000006-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000009-000006\\\",\\\"module\\\":[],\\\"name\\\":\\\"渠道妥投时效报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/deliveryAging/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000007-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000007-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000009-000007\\\",\\\"module\\\":[],\\\"name\\\":\\\"妥投率天数报表\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/deliveredRate/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000008-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"002-000009-000008\\\",\\\"module\\\":[],\\\"name\\\":\\\"多渠道妥投率统计\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/multiChannel/\\\",\\\"menu\\\":[]},{\\\"function\\\":[{\\\"key\\\":\\\"002-000009-000009-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"002-000009-000009-002\\\",\\\"name\\\":\\\"导出\\\"}],\\\"key\\\":\\\"002-000009-000009\\\",\\\"module\\\":[],\\\"name\\\":\\\"渠道运费试算功能查询\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/lgtconfig/report/freightCalculation/\\\",\\\"menu\\\":[]}],\\\"name\\\":\\\"物流报表\\\",\\\"url\\\":\\\"/lgtconfig/\\\",\\\"menu\\\":[],\\\"function\\\":[]}],\\\"system\\\":{\\\"key\\\":\\\"002\\\",\\\"name\\\":\\\"物流\\\",\\\"url\\\":\\\"/lgtconfig/\\\"}}\"}],\"positionIds\":[\"2\"]},\"ticket\":\"7d9c7c16fa2302ad3eb2b71a371873ea\",\"operator\":\"ceshi11a\",\"personName\":\"测试11a\",\"funcVersion\":\"d01836394d1d7d8e728354d3a5ae24b6\",\"moduleUrl\":\"/user/rolemanagement/pmGroup/edit/\",\"requestId\":\"513fc01ac7955ad6d72dd0\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.addOrUpdatePermissionGroup(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test4() {
        String json = "{\"data\":{\"positionId\":\"123\",\"selectedContext\":[{\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"000-000001\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"000-000001-000003-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"000-000001-000003\\\",\\\"module\\\":[],\\\"name\\\":\\\"绩效考评\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/index/console/gradeevaluate/\\\"},{\\\"function\\\":[{\\\"key\\\":\\\"000-000001-000004-001\\\",\\\"name\\\":\\\"查看\\\"},{\\\"key\\\":\\\"000-000001-000004-002\\\",\\\"name\\\":\\\"下载通知\\\"}],\\\"key\\\":\\\"000-000001-000004\\\",\\\"module\\\":[],\\\"name\\\":\\\"刊登通知\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/index/console/publishnotification/\\\"}],\\\"name\\\":\\\"工作台\\\",\\\"url\\\":\\\"/index/\\\"},{\\\"key\\\":\\\"000-000003\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"000-000003-000001-001\\\",\\\"name\\\":\\\"编辑\\\"}],\\\"key\\\":\\\"000-000003-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"新功能介绍\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/index/notice/introduction/\\\"}],\\\"name\\\":\\\"公告栏\\\",\\\"url\\\":\\\"/index/\\\"}],\\\"system\\\":{\\\"key\\\":\\\"000\\\",\\\"name\\\":\\\"首页\\\",\\\"url\\\":\\\"/index/\\\"}}\",\"sysKey\":\"001\"}]}}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.savePositionPermission(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test5() {
        String json = "{\"data\":{\"pageNumber\":1,\"pageData\":100},\"ticket\":\"617791bb38d16a793e78ba9cfb29e18c\",\"operator\":\"wensheng\",\"personName\":\"dingjinfeng\",\"funcVersion\":\"308a2ccc6be755b9d8630549541eb270\",\"moduleUrl\":\"/account/manage/appeal/\",\"requestId\":\"11051519151069194701f431c2a7b00f\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPermissionGroupByUser(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test6() {
        Map map = new HashMap();
        map.put("groupId", "1");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.deletePermissionGroup(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test7() {
        String json = "{\"data\":{\"groupName\":\"test\",\"selectedContext\":[{\"sysKey\":\"017\",\"sysContext\":\"{\\\"menu\\\":[{\\\"key\\\":\\\"017-000002\\\",\\\"module\\\":[{\\\"function\\\":[{\\\"key\\\":\\\"017-000002-000001-001\\\",\\\"name\\\":\\\"查看\\\"}],\\\"key\\\":\\\"017-000002-000001\\\",\\\"module\\\":[],\\\"name\\\":\\\"主体管理\\\",\\\"pageFullPathName\\\":\\\"\\\",\\\"show\\\":1,\\\"url\\\":\\\"/account/material/mainBody/\\\"}],\\\"name\\\":\\\"账号资料\\\",\\\"url\\\":\\\"/account/\\\"}],\\\"system\\\":{\\\"key\\\":\\\"017\\\",\\\"name\\\":\\\"账号\\\",\\\"url\\\":\\\"/account/\\\"}}\"}],\"positions\":[]},\"ticket\":\"7b59d8dba4d3bcfaec01f054a3a10bb5\",\"operator\":\"chenhuili\",\"personName\":\"陈慧丽\",\"funcVersion\":\"d4b849668905dbf8c0e7482b2e013cb1\",\"moduleUrl\":\"/user/rolemanagement/pmGroup/edit/\",\"requestId\":\"12091111537941783dd4daad6995ab07\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.addOrUpdatePermissionGroup(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }
    @Test
    public void test8() {
        Map map = new HashMap();
        map.put("groupId", "1607493661846000004");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPermissionGroupInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test9() {
        String json = "{\"data\":{\"lstPermitKey\":[\"001-000006-000010-001\"],\"pageData\":20,\"pageNumber\":1},\"ticket\":\"8c260b1d4f508a05a4d551fe9b995c07\",\"operator\":\"wensheng\",\"personName\":\"文胜\",\"funcVersion\":\"96a0b0b0048712e3c51623c7520a9a6f\",\"moduleUrl\":\"/user/userAuthorization/\",\"requestId\":\"1040380139a6e64167b579075\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPositionInfoByPermitKey(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test10() {
        Map map = new HashMap();
        map.put("positionId", "123");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPositionPermission(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test11() {
        String json ="{\"data\":{\"pageData\":100,\"pageNumber\":1,\"lstPermitKey\":[\"000-000001-000003-001\",\"000-000001-000004-001\"],\"positionIds\":[\"1606805482420000065\"]}}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPositionInfoByPermitKey(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test12() {
        String json ="{\"data\":{\"lstPermitKey\":[\"001-000006-000001-001\",\"001-000006-000001-002\"],\"positionIds\":[\"1606805482420000065\"]},\"ticket\":\"4e02a31990b176fcc5390aff3ba4bc44\",\"operator\":\"wensheng\",\"personName\":\"文胜\",\"funcVersion\":\"96a0b0b0048712e3c51623c7520a9a6f\",\"moduleUrl\":\"/user/userAuthorization/\",\"requestId\":\"5003996f2ff6a03bdaab38bc63\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.exportPositionInfoByPermitKey(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

}
