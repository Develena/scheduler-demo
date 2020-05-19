package com.example.scheduler.config;

import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobResponse;
import com.example.scheduler.entity.JobStatusResponse;
import com.example.scheduler.util.BeanUtil;
import com.example.scheduler.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Quartz Manager
 * 수행 시점을 CronTrigger로 동적으로 설정하여 Job Scheduling.
 */
@Slf4j
@Component
public class QuartzManager {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     *  Configure Trigger : 수행 시점에 관한 정보 설정
     *  Quartz의 CronTrigger는 Spring의 CronTriggerFactoryBean으로 구현됨.
     */
    public Trigger createTrigger(JobRequest jobRequest) {
        String cronExpression = jobRequest.getCronExpression();
        LocalDateTime startDateAt = jobRequest.getStartDateAt();

        if (!StringUtils.isEmpty(cronExpression)) {
            return createCronTrigger(jobRequest);

        }else if(startDateAt != null){
            return createSimpleTrigger(jobRequest);
        }

        throw new IllegalStateException("unsupported trigger descriptor");
    }

    private Trigger createCronTrigger(JobRequest jobRequest) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName(jobRequest.getJobName());
        factoryBean.setGroup(jobRequest.getJobGroup()); // DEFAULT
        factoryBean.setCronExpression(jobRequest.getCronExpression());
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

        JobDetail jobDetail = (JobDetail) BeanUtil.getBean(jobRequest.getJobName());
        factoryBean.setJobDetail(jobDetail);

        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return factoryBean.getObject();
    }

    private Trigger createSimpleTrigger(JobRequest jobRequest) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setName(jobRequest.getJobName());
        factoryBean.setGroup(jobRequest.getJobGroup());
        factoryBean.setStartTime(Date.from(jobRequest.getStartDateAt().atZone(ZoneId.systemDefault()).toInstant()));
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        factoryBean.setRepeatInterval(jobRequest.getRepeatIntervalInSeconds() * 1000); //ms 단위임
        factoryBean.setRepeatCount(jobRequest.getRepeatCount());

        JobDetail jobDetail = (JobDetail) BeanUtil.getBean(jobRequest.getJobName());
        factoryBean.setJobDetail(jobDetail);

        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /**
     * Scheduler 에 Job 등록
     * @param jobRequest
     * @return
     */
    public boolean addBatchJob(JobRequest jobRequest) {

        JobDetail jobDetail = (JobDetail) BeanUtil.getBean(jobRequest.getJobName());
        JobKey jobKey = jobDetail.getKey();
        try {
            Trigger trigger = createTrigger(jobRequest);
            if(schedulerFactoryBean.getScheduler().checkExists(jobKey)){
                Date dt = schedulerFactoryBean.getScheduler().scheduleJob(trigger);
                log.info("### Job with jobKey : {} scheduled successfully at date : {}", jobKey, dt);
                return true;
            }
        } catch (SchedulerException e) {
            log.error("error occurred while scheduling with jobKey : {}", jobKey, e);
        }

        return false;
    }

    /**
     * Scheduled Job 삭제
     * @param jobKey
     * @return
     */
    public boolean deleteJob(JobKey jobKey){
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] deleting job with jobKey : {}", jobKey);
        try {
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * Scheduled Job 일시중지.
     * @param jobKey
     * @return
     */
    public boolean pauseJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] pausing job with jobKey : {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * Scheduled Job 재시작(un-pause).
     * @param jobKey
     * @return
     */
    public boolean resumeJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] resuming job with jobKey : {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while resuming job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * Scheduler가 관리하는 모든 Job 조회
     * @return
     */
    public JobStatusResponse getAllJobs(){
        JobResponse jobResponse;
        JobStatusResponse jobStatusResponse = new JobStatusResponse();
        List<JobResponse> jobs = new ArrayList<>();
        int numOfRunningJobs = 0;
        int numOfGroups = 0;
        int numOfAllJobs = 0;

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                numOfGroups++;
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

                    if(triggers.isEmpty()){
                        jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
                        jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
                        jobStatusResponse.setNumOfGroups(numOfGroups);
                        jobStatusResponse.setJobs(jobs);
                        return jobStatusResponse;
                    }

                    jobResponse = JobResponse.builder()
                            .jobName(jobKey.getName())
                            .groupName(jobKey.getGroup())
                            .scheduleTime(DateTimeUtils.toString(triggers.get(0).getStartTime()))
                            .lastFiredTime(DateTimeUtils.toString(triggers.get(0).getPreviousFireTime()))
                            .nextFireTime(DateTimeUtils.toString(triggers.get(0).getNextFireTime()))
                            .build();

                    if (isJobRunning(jobKey)) {
                        jobResponse.setJobStatus("RUNNING");
                        numOfRunningJobs++;
                    } else {
                        String jobState = getJobState(jobKey);
                        jobResponse.setJobStatus(jobState);
                    }
                    numOfAllJobs++;
                    jobs.add(jobResponse);
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error while fetching all job info", e);
        }

        jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
        jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
        jobStatusResponse.setNumOfGroups(numOfGroups);
        jobStatusResponse.setJobs(jobs);
        return jobStatusResponse;
    }

    /**
     * Job의 Scheduling 상태 : true / false
     * @param jobKey
     * @return
     */
    public boolean isJobRunning(JobKey jobKey){
        try {
            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    if (jobKey.getName().equals(jobCtx.getJobDetail().getKey().getName())) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job with jobKey : {}", jobKey, e);
        }
        return false;

    }

    /**
     * Job 존재 유무 : true / false
     * @param jobKey
     * @return
     */
    public boolean isJobExsits(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job exists :: jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * Job 상태 조회
     * @param jobKey
     * @return
     */
    public String getJobState(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());

            if (triggers != null && triggers.size() > 0) {
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    if (Trigger.TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                    return triggerState.name().toUpperCase();
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] Error occurred while getting job state with jobKey : {}", jobKey, e);
        }
        return null;
    }


}
