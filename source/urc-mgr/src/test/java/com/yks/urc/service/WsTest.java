package com.yks.urc.service;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.userValidate.bp.impl.UserValidateBp;
import com.yks.urc.vo.ResultVO;
import org.apache.curator.framework.CuratorFrameworkFactory;
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
        map2.put("data",map);
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
        map.put("isSupperAdmin", "0");
        Map map2 = new HashMap();
        map2.put("data",map);
        String json = StringUtility.toJSONString(map2);
        MotanSession.initialSession(json);
        ResultVO resultVO = urcMgr.setSupperAdmin(json);
        System.out.println(StringUtility.toJSONString(resultVO));

    }

}
