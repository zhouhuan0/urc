/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author lwx
 * @create 2018/11/26
 * @since 1.0.0
 */
package com.yks.urc.user.bp.impl;

import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.mapper.IUserLoginLogMapper;
import com.yks.urc.user.bp.api.IUserLogBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class UserLogBpImpl implements IUserLogBp {

    private static Logger logger = LoggerFactory.getLogger(UserLogBpImpl.class);
    ExecutorService service = Executors.newFixedThreadPool(4);

    @Autowired
    private IUserLoginLogMapper logMapper;


    @Override
    public void insertLog(UserLoginLogDO logDO) {
        // 异步入库
        service.submit(new Runnable() {
            @Override
            public void run() {
                    logMapper.insertLogs(logDO);
            }
        });
        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
          logger.error("日志入库执行异常",e);
        }
    }
}
