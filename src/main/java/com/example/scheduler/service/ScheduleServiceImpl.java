package com.example.scheduler.service;


import com.example.scheduler.config.QuartzManager;
import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService{

    @Autowired
    private QuartzManager quartzManager;

    @Override
    public JobStatusResponse getAllJobs() {
        return quartzManager.getAllJobs();
    }

    @Override
    public boolean isJobRunning(JobKey jobKey) {
        return quartzManager.isJobRunning(jobKey);
    }

    @Override
    public boolean isJobExists(JobKey jobKey) {
        return quartzManager.isJobExsits(jobKey);
    }

    @Override
    public boolean addJob(JobRequest jobRequest) {
        return quartzManager.addBatchJob(jobRequest);
    }

    @Override
    public boolean deleteJob(JobKey jobKey) {

        return quartzManager.deleteJob(jobKey);
    }

    @Override
    public boolean pauseJob(JobKey jobKey) {
        return quartzManager.pauseJob(jobKey);
    }

    @Override
    public boolean resumeJob(JobKey jobKey) {
        return quartzManager.resumeJob(jobKey);
    }

    @Override
    public String getJobState(JobKey jobKey) {
        return quartzManager.getJobState(jobKey);
    }

//    @Override
//    public boolean testJobs() {
//
//        JobRequest jobRequest = new JobRequest();
//        jobRequest.setJobName("SampleBatchJobDetail");
//        jobRequest.setJobGroup("SampleGroup");
//        jobRequest.setCronExpression("0 0/1 * 1/1 * ? *");
//
//        boolean result = quartzManager.addBatchJob(jobRequest);
//
//        return result;
//    }
}
