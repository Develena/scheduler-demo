package com.example.scheduler.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class StepTwo implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("### Second Step start....");
        // @Todo : implement your step code.
        log.info("### Second Step(your code) >>>>....");

        log.info("### Second STep done....");
        return RepeatStatus.FINISHED;
    }
}
