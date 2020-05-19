package com.example.scheduler.jobs;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Create Quartz Job : QuartzJobBean 이 Spring Batch Job을 Launch.
 */
@Slf4j
public class BatchJobLauncher extends QuartzJobBean {

    @Getter
    @Setter
    private String jobName;

    @Getter
    @Setter
    private JobLauncher jobLauncher;

    @Getter
    @Setter
    private JobLocator jobLocator;

    private volatile Thread currThread;


    // execute job
    // 1. in this case, only one parameter reference :  Job Name.
    // 2. Job Name -> JobLocator 로 JobRegistry에 등록된 Job을 검색.
    // 3. JobLauncher를 통해 해당 Job을 실행함.
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        currThread = Thread.currentThread();

        try
        {
            Job job = jobLocator.getJob(jobName);
            JobParameters params = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, params);
            log.info("### {}_{} was completed successfully", job.getName(), jobExecution.getId());
            log.info("### Current Thread : {}", currThread.getName());
        }
        catch (Exception e) {
            log.error("Encountered job execution exception!");
            e.printStackTrace();
        }
    }

}
