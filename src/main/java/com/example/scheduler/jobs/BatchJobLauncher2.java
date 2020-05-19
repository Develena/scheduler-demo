//package com.example.scheduler.jobs;
//
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.SchedulerException;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.configuration.JobLocator;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.NoSuchJobException;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
///**
// * Create Quartz Job : QuartzJobBean 이 Spring Batch Job을 Launch.
// */
//@Slf4j
//public class BatchJobLauncher2 implements org.quartz.Job {
//
//    @Autowired
//    private JobLocator jobLocator;
//
//    @Autowired
//    private JobLauncher jobLauncher;
//
//
//    // execute job
//    // 1. in this case, only one parameter reference :  Job Name.
//    // 2. Job Name -> JobLocator 로 JobRegistry에 등록된 Job을 검색.
//    // 3. JobLauncher를 통해 해당 Job을 실행함.
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        try {
//            String jobName = BatchHelper.getJobName(context.getMergedJobDataMap());
//            log.info("[{}] started.", jobName);
//            JobParameters jobParameters = BatchHelper.getJobParameters(context);
//            jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
//            log.info("[{}] completed.", jobName);
//        } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | SchedulerException e) {
//            log.error("job execution exception! - {}", e.getCause());
//            throw new JobExecutionException();
//        }
//    }
//}
