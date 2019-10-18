package com.yks.pls.task.quatz;

import com.yks.urc.lock.bp.api.ILockBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTask {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public void doTask(String param) {
        ITaskProvider taskProvider = QuatzBootstrap.getBean(ITaskProvider.class);
        try {
            if (!isNeedLock()) {
                taskProvider.writeInfoLog("task start");
                doTaskSub(param);
                taskProvider.writeInfoLog("task finish");
                return;
            }

            ILockBp lockBp = QuatzBootstrap.getBean(ILockBp.class);
            if (lockBp.tryLock(this.getClass().getSimpleName())) {
                try {
                    taskProvider.writeInfoLog("task start");
                    doTaskSub(param);
                    taskProvider.writeInfoLog("task finish");
                } catch (Exception ex) {
                    logger.error(String.format("doTask ERROR:%s", param), ex);
                    taskProvider.writeErrorLog(ex);
                } finally {
                    lockBp.unlock(this.getClass().getSimpleName());
                }
            } else {
                logger.info(String.format("not get lock:%s", this.getClass().getSimpleName()));
            }

        } catch (Exception e) {
            logger.error(String.format("doTask ERROR:%s", param), e);
            taskProvider.writeErrorLog(e);
        } finally {
//            QuatzBootstrap.getBean(IDbSourceBp.class).removeDb();
        }
    }

    protected Boolean isNeedLock() {
        return true;
    }

    protected abstract void doTaskSub(String param) throws Exception;
}
