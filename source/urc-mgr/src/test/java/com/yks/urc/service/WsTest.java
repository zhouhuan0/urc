package com.yks.urc.service;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.vo.ResultVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

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
        Map map = new HashMap();
        map.put("operator", "wensheng");
        map.put("positionId", "1606805482420000065");
        map.put("isSupperAdmin", "1");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.setSupperAdmin(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

    @Test
    public void test3() {
        Map map = new HashMap();
        map.put("operator", "wensheng");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.getUserAuthorizablePermissionForPosition(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test4() {
        String json = "{\"data\":{\"positionId\":\"123\",\"selectedContext\":[{\"sysContext\":\"zz\",\"sysKey\":\"zz\"},{\"sysContext\":\"ss\",\"sysKey\":\"ss\"}]}}";
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.savePositionPermission(json);
        System.out.println(StringUtility.toJSONString(resultVO));
    }

    @Test
    public void test5() {
        Map map = new HashMap();
        map.put("pageNumber", 1);
        map.put("pageData", 100);
        // map.put("groupName", "wsGroup");
        map.put("userName", "wensheng");
        Map map2 = new HashMap();
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
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
}
