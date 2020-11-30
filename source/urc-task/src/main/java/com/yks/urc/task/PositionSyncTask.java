/*
 * 文件名：PositionSyncTask.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/11/30
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.task;

import com.yks.pls.task.quatz.BaseTask;
import com.yks.urc.hr.bp.api.IHrBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/11/30
 * @see PositionSyncTask
 * @since JDK1.8
 */
@Component
public class PositionSyncTask extends BaseTask {
    private static final Logger logger = LoggerFactory.getLogger(PositionSyncTask.class);

    @Autowired
    private IHrBp hrBp;

    @Override
    protected void doTaskSub(String param) throws Exception {
        try {
            logger.info("岗位数据同步开始");
            hrBp.positionSync();
            logger.info("岗位数据同步完成");
        } catch (Exception e) {
            logger.error("岗位数据同步出错:", e);
        }
    }
}
