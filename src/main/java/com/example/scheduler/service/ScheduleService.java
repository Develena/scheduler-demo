package com.example.scheduler.service;

import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobStatusResponse;
import org.quartz.Job;
import org.quartz.JobKey;

public interface ScheduleService {

    JobStatusResponse getAllJobs();

    boolean isJobRunning(JobKey jobKey);

    boolean isJobExists(JobKey jobKey);

    boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass);

    boolean deleteJob(JobKey jobKey);

    boolean pauseJob(JobKey jobKey);

    boolean resumeJob(JobKey jobKey);

    String getJobState(JobKey jobKey);

    String testJobs();

}
