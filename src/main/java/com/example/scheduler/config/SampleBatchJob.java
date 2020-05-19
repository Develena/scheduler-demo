package com.example.scheduler.config;

import com.example.scheduler.step.StepOne;
import com.example.scheduler.step.StepTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create Spring Batch Jobs : Spring Batch Job을 정의.
 * 1. Job 설정, 2. Step 설정, 3. Reader, Processor, Writer 설정
 * - 자체 CronExpression을 정의하는 경우.
 */
@Configuration
@EnableBatchProcessing
public class SampleBatchJob {

    public static final String JOB_NAME = "SampleBatchJob";
    public static final String JOBDETAIL_NAME = "SampleBatchJobDetail";
    public static final String JOB_GROUP = "SampleGroup";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepOne(){
        return stepBuilderFactory.get("stepOne")
                .tasklet(new StepOne())
                .build();
    }

    @Bean
    public Step stepTwo(){
        return stepBuilderFactory.get("stepTwo")
                .tasklet(new StepTwo())
                .build();
    }

    @Bean(name=JOB_NAME)
    public Job sampleBatchJob(){
        return jobBuilderFactory.get(JOB_NAME)
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

//    @Bean(name="BatchJobTwo")
//    public Job demoJobTwo(){
//        return jobBuilderFactory.get("BatchJobTwo")
//                .flow(stepOne())
//                .build()
//                .build();
//    }


}
