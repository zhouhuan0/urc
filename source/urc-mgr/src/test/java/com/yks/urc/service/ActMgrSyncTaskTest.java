package com.yks.urc.service;

import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.task.ActMgrSyncTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ActMgrSyncTaskTest extends BaseServiceTest {
    @Autowired
    private ActMgrSyncTask actMgrSyncTask;

    @Autowired
    IActMgrBp actMgrBp;

    @Test
    public void test1() throws Exception {
        String dtStart = "2020-07-28 00:00:00";
        String dtEnd = "2020-09-28 00:00:00";
//        actMgrBp.syncAct(dtStart, dtEnd);
        while (true) {
            actMgrSyncTask.doTask("{\n" +
                    "    \"minutes\": 600\n" +
                    "}");
            Thread.sleep(1000L);
        }
    }
}
