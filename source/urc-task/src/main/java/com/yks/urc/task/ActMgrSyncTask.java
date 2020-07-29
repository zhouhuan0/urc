package com.yks.urc.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.pls.task.quatz.BaseTask;
import com.yks.urc.entity.PermitRefreshTaskVO;
import com.yks.urc.enums.PermitRefreshTaskStatusEnum;
import com.yks.urc.enums.PermitRefreshTaskTypeEnum;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPermitRefreshTaskMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitInverseQueryBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.sellerid.bp.api.IActMgrBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Component
public class ActMgrSyncTask extends BaseTask {
    @Autowired
    IActMgrBp actMgrBp;

    @Override
    protected void doTaskSub(String param) throws Exception {
        actMgrBp.doAccountSyncTask(param);
    }
}
