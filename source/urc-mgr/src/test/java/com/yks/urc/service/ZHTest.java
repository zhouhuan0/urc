/*
 * 文件名：ZHTest.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/12/5
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.service;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.task.PositionSyncTask;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/12/5
 * @see ZHTest
 * @since JDK1.8
 */
public class ZHTest extends BaseServiceTest {
    @Autowired
    private PositionSyncTask positionSyncTask;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUrcMgr urcMgr;
    @Test
    public void testPositionSyncTask(){
        positionSyncTask.doTask("");
    }

    @Test
    public void test_getUsersByUserInfo(){
        UserVO userVO = new UserVO();
        userVO.userName="zhouhuan";
        ResultVO<PageResultVO> i = userService.getUsersByUserInfo("zhouhuan", userVO, "1", "10");
        System.out.println("=================");
    }

    @Test
    public void test_systemInfo(){
        Map map = new HashMap();
        map.put("pageNumber", 1);
        map.put("pageData", 100);
        map.put("sysKey", "001");
        map.put("remark", "测试");
        map.put("status", 1);
        map.put("dataAdministrators", Arrays.asList("zhouhuan","wensheng"));
        map.put("functionAdministrators", Arrays.asList("zhouhuan","wensheng"));
        Map map2 = new HashMap();
        map2.put("operator", "zhouhuan");
        map2.put("data", map);
        String json = StringUtility.toJSONString(map2);
        //ResultVO systemInfo = urcMgr.getSystemInfo(json);
        //ResultVO system = urcMgr.getSystem("");
        //ResultVO systemPermission = urcMgr.getSystemPermission(json);
        ResultVO resultVO = urcMgr.editSystemInfo(json);
        System.out.println("=================");
    }

    @Test
    public void test_FuncPermit(){
        Map map = new HashMap();
        map.put("id", "85");
        map.put("name", "testZhou");
        //map.put("sysKeys", "001");
        Map map2 = new HashMap();
        map2.put("operator", "zhouhuan");
        map2.put("data", Arrays.asList(map));
        String jsonStr = StringUtility.toJSONString(map2);
       // ResultVO<GetAllFuncPermitRespVO> allFuncPermit = userService.getAllFuncPermit(jsonStr);
       // ResultVO allFuncPermitForOtherSystem = urcMgr.getAllFuncPermitForOtherSystem(jsonStr);
        ResultVO resultVO = urcMgr.updatePosition(jsonStr);
        System.out.println("=================");
    }
}
