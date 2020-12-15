package com.yks.urc.task;

import com.yks.pls.task.quatz.BaseTask;
import com.yks.urc.service.api.ICommonPermissionService;
import com.yks.urc.service.api.IPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonPermissionTask extends BaseTask {
    private static final Logger logger = LoggerFactory.getLogger(CommonPermissionTask.class);

    @Autowired
    private ICommonPermissionService commonPermissionService;

    /**
     * 给新员工的加通用权限
     * @param param
     * @throws Exception
     */
    @Override
    protected void doTaskSub(String param) throws Exception {
        commonPermissionService.authorize();
    }
}
