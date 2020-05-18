package com.example.scheduler.config;

import com.example.scheduler.jobs.BatchJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * Quartz Configuration : JobDetails & Triggers 구성.
 */
@Slf4j
@Configuration
public class QuartzConfig {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    // 1. JobRegistry(Spring batch)로 Job bean 등록하는 BeanPostProccessor 생성.
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /*
     2. Configure JobDetail.
     //   Configure JobDetailBean
      Quartz의 JobDetail -> Spring 에서는 JobDetailFactoryBean.
      수행 내용인 Job은 QuartzJobBean을 구현(implementing)해야 함.
      JobName, GroupName 설정.
      Job 수행에 파라미터가 필요할 경우 -> setJobDataAsMap() 이용하여 전달.
   */
    @Bean
    public JobDetail jobOneDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "demoJobOne");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(BatchJobLauncher.class)
                .withIdentity("demoJobOne")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail jobTwoDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "demoJobTwo");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(BatchJobLauncher.class)
                .withIdentity("demoJobTwo")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }






    /*
     2. Configure Trigger
        // 수행 시점에 관한 정보 설정 -> Job is scheduled after every 1 minute
        // Quartz의 CronTrigger는 Spring의 CronTriggerFactoryBean으로 구현됨.

     */
//   @Bean
//    public Trigger jobOneTrigger()
//    {
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
//                .simpleSchedule()
//                .withIntervalInSeconds(10)
//                .repeatForever();
//
//        return TriggerBuilder
//                .newTrigger()
//                .forJob(jobOneDetail())
//                .withIdentity("jobOneTrigger")
//                .withSchedule(scheduleBuilder)
//                .build();
//    }

//    @Bean
//    public CronTriggerFactoryBean cronTriggerFactoryBean() {
//        CronTriggerFactoryBean ctFactory = new CronTriggerFactoryBean();
//        ctFactory.setJobDetail(jobOneDetail());
//        ctFactory.setStartDelay(1000); // after 10s
//        ctFactory.setName("cron_trigger1");
//        ctFactory.setGroup("cron_group");
//        ctFactory.setCronExpression("0 0/1 * 1/1 * ? *");
//        return ctFactory;
//    }


//   @Bean
//    public Trigger jobTwoTrigger()
//    {
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
//                .simpleSchedule()
//                .withIntervalInSeconds(20)
//                .repeatForever();
//
//        return TriggerBuilder
//                .newTrigger()
//                .forJob(jobTwoDetail())
//                .withIdentity("jobTwoTrigger")
//                .withSchedule(
//                )
//                .build();
//    }

    /*
         3. Quartz Scheduler 는 Spring의 SchedulerFactoryBean 으로 구현.
            register all the triggers. (CronTriggerFactoryBean이 등록됨)
    */
//   @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException
//    {
//        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
//        scheduler.setTriggers(jobOneTrigger(), jobTwoTrigger());
//        scheduler.setQuartzProperties(quartzProperties());
//        scheduler.setJobDetails(jobOneDetail(), jobTwoDetail());
//        return scheduler;
//    }
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
//        scheduler.setTriggers(cronTriggerFactoryBean().getObject());
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
