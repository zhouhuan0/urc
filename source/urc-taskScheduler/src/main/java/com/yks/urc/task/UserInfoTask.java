/**
 * 〈一句话功能简述〉<br>
 * 〈请求UserInfo定时任务〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/9
 * @since 1.0.0
 */
package com.yks.urc.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yks.urc.user.bp.impl.UserBpImpl;

//@Component
public class UserInfoTask {
    private static Logger logger = LoggerFactory.getLogger(UserInfoTask.class);

    @Autowired
    private UserBpImpl userBp;

    public static final String SYSTEM = "system";

    @Scheduled(cron = "0/5 * * * * ?")
    public void executeGetUserInfo() {
        logger.info("开始同步数据");
        try {
            userBp.SynUserFromUserInfo(SYSTEM);
        } catch (Exception e) {
           logger.error(e.getMessage());
        }
    }
}
