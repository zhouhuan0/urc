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
        String json = "{\"data\":{},\"ticket\":\"5adf338494b39326142c88e777cf2db5\",\"operator\":\"wensheng\",\"personName\":\"文胜\",\"funcVersion\":\"557a74e41f37da5c73a4193e7f008809\",\"moduleUrl\":\"/user/rolemanagement/\",\"requestId\":\"120815320501962f1fcd61d526a84c40\",\"deviceName\":\"Chrome浏览器\"}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getUserAuthorizablePermissionForPosition(json);
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
        String json = "{\"data\":{\"pageNumber\":1,\"pageData\":100,\"userName\":\"wensheng\"},\"ticket\":\"617791bb38d16a793e78ba9cfb29e18c\",\"operator\":\"wensheng\",\"personName\":\"dingjinfeng\",\"funcVersion\":\"308a2ccc6be755b9d8630549541eb270\",\"moduleUrl\":\"/account/manage/appeal/\",\"requestId\":\"11051519151069194701f431c2a7b00f\",\"deviceName\":\"Chrome浏览器\"}";
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
        String json = "{\"data\":{\"groupId\":\"123\",\"groupName\":\"123\",\"positionIds\":[\"123\",\"321\"],\"selectedContext\":[{\"sysContext\":\"zz\",\"sysKey\":\"zz\"},{\"sysContext\":\"ss\",\"sysKey\":\"ss\"}]}}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.addOrUpdatePermissionGroup(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }
    @Test
    public void test8() {
        Map map = new HashMap();
        map.put("groupId", "123");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPermissionGroupInfo(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test9() {
        Map map = new HashMap();
        map.put("positionName", "");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getPositionList(json);
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
        String json ="{\"data\":{\"pageData\":100,\"pageNumber\":1,\"lstPermitKey\":[\"000-000001-000003-001\",\"000-000001-000004-001\"],\"positionIds\":[\"1606805482420000065\"]}}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.exportPositionInfoByPermitKey(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

}
