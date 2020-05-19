package com.example.scheduler.config;

import com.example.scheduler.jobs.BatchJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;


/**
 * Quartz Configuration : JobDetails & Triggers 구성.
 */
@Slf4j
@Configuration
public class QuartzConfiguration {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    /**
     * JobResistryBeanPostProcessor : JobRegistry에 Job을 자동으로 등록
     * @param jobRegistry
     * @return
     */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /**
     * 2. Configure All JobDetail(Quartz) / JobDetailBean(Quartz+Spring)
     *    Job 수행에 파라미터가 필요할 경우 -> setJobDataAsMap() 이용하여 전달.
     *
     * @return JobDetail
     */
    @Bean(name=SampleBatchJob.JOBDETAIL_NAME)
    public JobDetail sampleBatchJobDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", SampleBatchJob.JOB_NAME);
        jobDataMap.put("jobGroup", SampleBatchJob.JOB_GROUP);
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(BatchJobLauncher.class)
                .withIdentity(SampleBatchJob.JOB_NAME)
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    // ... 등록된 모든 Spring Batch Job을 JobDetail @Bean으로 등록한다.

    /**
     * 3. Quartz Scheduler Customizing : Spring의 SchedulerFactoryBean
     *
     * @Todo: BatchConfiguration 비교, 확장.
     * @return
     */
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
//        scheduler.setSchedulerName("SchedulerDemoProject-0.0.1");
//        // Register JobFactory
////        scheduler.setJobFactory(jobFactory);
//        //Graceful Shutdown 을 위한 설정으로 Job 이 완료될 때까지 Shutdown 을 대기하는 설정
//        scheduler.setWaitForJobsToCompleteOnShutdown(true);
//        //Job Detail 데이터 Overwrite 유무
//        scheduler.setOverwriteExistingJobs(true);
//        // scheduler properties 셋팅.
//        scheduler.setQuartzProperties(quartzProperties());
//        // 모든 JobDetail 등록 -> JobKey로 JobDetail 핸들링 가능.
//        scheduler.setJobDetails(sampleBatchJobDetail());
//        // Trigger도 등록 가능하나, 해당 프로젝트에서는 제어 구현을 위해 동적으로 등록할 예정.
////        scheduler.setTriggers(jobOneTrigger(), jobTwoTrigger());
//        return scheduler;
//    }

    @Bean
    public Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
