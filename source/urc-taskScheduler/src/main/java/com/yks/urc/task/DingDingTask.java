package com.yks.urc.task;

import com.yks.pls.task.quatz.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.service.impl.PersonServiceImpl;

@Component
public class DingDingTask extends BaseTask {
    private static final Logger logger = LoggerFactory.getLogger(DingDingTask.class);

    @Autowired
    private IPersonService personService;

    @Override
    protected void doTaskSub(String param) throws Exception {
        personService.pullAndSaveDingDingInfo();
    }
}
