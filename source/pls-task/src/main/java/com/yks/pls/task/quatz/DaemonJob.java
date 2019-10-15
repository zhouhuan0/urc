package com.yks.pls.task.quatz;


import com.yks.urc.fw.StringUtility;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.springframework.util.CollectionUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DisallowConcurrentExecution
public class DaemonJob implements Job {
    static Logger logger = LoggerFactory.getLogger(DaemonJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<PlsEbayTaskDO> lstJobFromDb = getJobFromDb();
//        logger.info(String.format("getJobFromDb:%s", StringCommonUtils.toJSONString(lstJobFromDb)));
        handleStopJob(lstJobFromDb);
        handleNewJob(lstJobFromDb);
    }

    private void handleNewJob(List<PlsEbayTaskDO> lstJobFromDb) {
        if (CollectionUtils.isEmpty(lstJobFromDb)) return;

        for (PlsEbayTaskDO j : lstJobFromDb) {
            if (j == null || j.getIsEnabled() == false) {
                continue;
            }
            try {
                JobKey jobKey = new JobKey(j.getTaskName(), j.getTaskGroup());
                if (getScheduler().getJobDetail(jobKey) == null) {
                    addNewJob(j, NormalJob.class);
                }
            } catch (Exception ex) {
                logger.error(QuatzBootstrap.getBean(ISerializeBp.class).obj2Json(j), ex);
            }
        }
    }

    private static void addNewJob(PlsEbayTaskDO jobVO, Class<? extends Job> jobClass) {
        if (jobVO == null || StringUtils.isBlank(jobVO.getTaskGroup())
                || StringUtils.isBlank(jobVO.getTaskName())
                || StringUtility.isNullOrEmpty(jobVO.getCronExpression())
                || jobVO.getTriggerStartTime() == null)
            return;
        try {
            JobDetail jobDetail = createJobDetail(jobVO, jobClass);
            //创建一trigger
            Trigger trigger = TriggerBuilder.newTrigger().
                    withIdentity(String.format("trigger_%s", jobVO.getTaskName()), jobVO.getTaskGroup()).
                    startAt(jobVO.getTriggerStartTime()).
                    withSchedule(CronScheduleBuilder.cronSchedule(jobVO.getCronExpression())).build();
            getScheduler().scheduleJob(jobDetail, trigger);
        } catch (Exception ex) {
            logger.error(QuatzBootstrap.getBean(ISerializeBp.class).obj2Json(jobVO), ex);
        }
    }

    private static Scheduler sche = null;

    private static Scheduler getScheduler() {
        return sche;
    }

    private static void startScheduelr(String threadNamePrefix, Integer threadCount) {
        try {
            DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
//            factory.createVolatileScheduler(10);
            SimpleThreadPool threadPool = new SimpleThreadPool(threadCount, 1);
            threadPool.setThreadNamePrefix(threadNamePrefix);
            threadPool.initialize();
            JobStore jobStore = new RAMJobStore();
            factory.createScheduler(threadPool, jobStore);

            sche = factory.getScheduler();
//            sche = factory.getScheduler("plsDirectScheduler");
            sche.start();
        } catch (Exception ex) {
            logger.error("startScheduelr", ex);
        }
    }

    private void handleStopJob(List<PlsEbayTaskDO> lstJobFromDb) {
        try {
            Scheduler scheduler = getScheduler();
            List<String> jobGroups = scheduler.getJobGroupNames();
            for (int i = 0; i < jobGroups.size(); i++) {

                String jg = jobGroups.get(i);
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jg))) {
                    // 如果 jobKey 不存在或被禁用，则停止
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if (jobDetail == null) {
                        continue;
                    }
                    PlsEbayTaskDO taskFromScheduler = getPlsEbayTaskDO(jobDetail);
                    if (isJobShouldStop(taskFromScheduler, lstJobFromDb)) {
                        scheduler.deleteJob(jobKey);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("handleStopJob", ex);
        }
    }

    /**
     * 是否要停止定时任务：在db不存在，或被禁用返回true
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2018/9/24 8:39
     */
    private Boolean isJobShouldStop(PlsEbayTaskDO taskFromScheduler, List<PlsEbayTaskDO> lstJobFromDb) {
        if (CollectionUtils.isEmpty(lstJobFromDb) || taskFromScheduler == null) return true;

        for (PlsEbayTaskDO j : lstJobFromDb) {
            if (j == null) {
                continue;
            }
            if (StringUtility.stringEqualsIgnoreCase(taskFromScheduler.md5, j.md5)) {
                return false;
            }
        }
        return true;
    }

    private String calcMd5(PlsEbayTaskDO task) {
        if (task == null) return StringUtils.EMPTY;
        return
                StringUtility.md5NoException(String.format("%s_%s_%s_%s_%s_%s_%s_%s",
                        task.getTaskId(),
                        task.getTaskGroup(), task.getTaskName(),
                        task.getCronExpression(), task.getIsEnabled(),
                        task.getBeanName(), task.getBeanMethod(), task.getBeanMethodParam()));
    }

    public List<PlsEbayTaskDO> getJobFromDb() {
        List<PlsEbayTaskDO> lstTask = QuatzBootstrap.getTaskDO();
        if (lstTask == null) {
            lstTask = new ArrayList<>();
        }
        lstTask.add(daemonJobVO);
        lstTask.forEach(m -> {
            m.md5 = calcMd5(m);
        });
        return lstTask;
    }

    static PlsEbayTaskDO daemonJobVO = new PlsEbayTaskDO();

    public static void addDaemonJob(String threadNamePrefix, Integer threadCount) {
        daemonJobVO.setIsEnabled(true);
        daemonJobVO.setCronExpression("0/20 * * * * ?");
        daemonJobVO.setTaskGroup(DaemonJob.class.getName());
        daemonJobVO.setTaskName(DaemonJob.class.getName());
        daemonJobVO.setTriggerStartTime(new Date());
        startScheduelr(threadNamePrefix,threadCount);
        addNewJob(daemonJobVO, DaemonJob.class);
    }

    private static String CONST_jobVO = "jobVO";

    public static JobDetail createJobDetail(PlsEbayTaskDO jobVO, Class<? extends Job> jobClass) {
        JobDataMap map = new JobDataMap();
        map.put(CONST_jobVO, jobVO);
        return JobBuilder.newJob(jobClass).usingJobData(map).
                withIdentity(jobVO.getTaskName(), jobVO.getTaskGroup()).build();
    }

    public static PlsEbayTaskDO getPlsEbayTaskDO(JobDetail jobDetail) {
        return (PlsEbayTaskDO) jobDetail.getJobDataMap().get(CONST_jobVO);
    }
}
