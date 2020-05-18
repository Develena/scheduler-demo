package com.example.scheduler.config;

import com.example.scheduler.step.StepOne;
import com.example.scheduler.step.StepTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create Spring Batch Jobs : Spring Batch Job을 정의.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

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

    @Bean(name="demoJobOne")
    public Job demoJobOne(){
        return jobBuilderFactory.get("demoJobOne")
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

    @Bean(name="demoJobTwo")
    public Job demoJobTwo(){
        return jobBuilderFactory.get("demoJobTwo")
                .flow(stepOne())
                .build()
                .build();
    }



}
