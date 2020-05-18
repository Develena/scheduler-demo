package com.example.scheduler.service;


import com.example.scheduler.config.QuartzConfig;
import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
public class SchedleServiceImpl implements ScheduleService{

    @Autowired
    private QuartzConfig config;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private JobDetail jobOneDetail;

    @Override
    public JobStatusResponse getAllJobs() {
        return null;
    }

    @Override
    public boolean isJobRunning(JobKey jobKey) {
        return false;
    }

    @Override
    public boolean isJobExists(JobKey jobKey) {
        return false;
    }

    @Override
    public boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass) {
        return false;
    }

    @Override
    public boolean deleteJob(JobKey jobKey) {
        return false;
    }

    @Override
    public boolean pauseJob(JobKey jobKey) {
        return false;
    }

    @Override
    public boolean resumeJob(JobKey jobKey) {
        return false;
    }

    @Override
    public String getJobState(JobKey jobKey) {
        return null;
    }

    @Override
    public String testJobs() {
        CronTriggerFactoryBean ctFactory = new CronTriggerFactoryBean();
        ctFactory.setStartDelay(1000); // after 10s
        ctFactory.setName("demoJobOne");
        ctFactory.setGroup("cron_group");
        ctFactory.setCronExpression("0 0/1 * 1/1 * ? *");
        ctFactory.setJobDetail(config.jobOneDetail());

        ctFactory.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        try {
            ctFactory.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if(schedulerFactoryBean.getScheduler().checkExists(config.jobOneDetail().getKey())){
                log.info("### jobkey {} is already exists.",config.jobOneDetail().getKey());
                Date dt = schedulerFactoryBean.getScheduler().scheduleJob(ctFactory.getObject());
                log.info("### Job with jobKey : {} scheduled successfully at date : {}", jobOneDetail.getKey(), dt);
                return "already";
            }

            Date dt = schedulerFactoryBean.getScheduler().scheduleJob(config.jobOneDetail(), ctFactory.getObject());
            log.info("### Job with jobKey : {} scheduled successfully at date : {}", jobOneDetail.getKey(), dt);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return "success";
    }
}
