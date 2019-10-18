package com.yks.pls.task.quatz;

import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

@DisallowConcurrentExecution
public class NormalJob implements Job {
    static Logger logger = LoggerFactory.getLogger(NormalJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        PlsEbayTaskDO jobVO = null;
        try {
            jobVO = DaemonJob.getPlsEbayTaskDO(context.getJobDetail());
//            logger.info(StringCommonUtils.toJSONString(jobVO));
            if (jobVO == null ||
                    StringUtils.isBlank(jobVO.getBeanName())) return;

            QuatzBootstrap.getBean(ITaskProvider.class).setTaskDO(jobVO);
            invokeMethod(QuatzBootstrap.getApplicationContext().getBean(jobVO.getBeanName()),
                    jobVO.getBeanMethod(), jobVO.getBeanMethodParam());
        } catch (Exception ex) {
            logger.error(QuatzBootstrap.getBean(ISerializeBp.class).obj2Json(jobVO), ex);
        }
        finally {
            QuatzBootstrap.getBean(ITaskProvider.class).removeTaskDO(jobVO);
        }
    }

    public static <T> T invokeMethod(Object target, String name, Object... args) {
        Assert.notNull(target, "Target object must not be null");
        Assert.hasText(name, "Method name must not be empty");

        try {
            MethodInvoker methodInvoker = new MethodInvoker();
            methodInvoker.setTargetObject(target);
            methodInvoker.setTargetMethod(name);
            methodInvoker.setArguments(args);
            methodInvoker.prepare();
            return (T) methodInvoker.invoke();
        } catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }
}
