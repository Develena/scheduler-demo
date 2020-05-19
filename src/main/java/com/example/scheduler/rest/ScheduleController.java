package com.example.scheduler.rest;

import com.example.scheduler.entity.ApiResponse;
import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobStatusResponse;
import com.example.scheduler.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/scheduler")
public class ScheduleController {

    //@Todo : delete 후 addScheduled 안되는 문제.

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping(value = "/job")
    public ResponseEntity<?> addScheduleJob(@RequestBody JobRequest jobRequest) {
        log.debug("add schedule job :: jobRequest : {}", jobRequest);
        if (jobRequest.getJobName() == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Require jobName"),
                    HttpStatus.BAD_REQUEST);
        }

        JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        log.debug("jobKey {}", jobKey);

        if(!scheduleService.isJobExists(jobKey)){
            scheduleService.addJob(jobRequest);
        }else{
            return new ResponseEntity<>(new ApiResponse(false, "Job already exits"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Job created successfully"), HttpStatus.CREATED);
    }

    // @Todo JobDetailName으로 동작
    @DeleteMapping(value= "/job")
    public ResponseEntity<?> deleteScheduleJob(@RequestBody JobRequest jobRequest) {

        JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        log.debug("jobKey {}", jobKey);

        if (scheduleService.isJobExists(jobKey)) {
            if (!scheduleService.isJobRunning(jobKey)) {
                scheduleService.deleteJob(jobKey);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, "Job already in running state"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Job does not exits"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Job deleted successfully"), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs")
    public JobStatusResponse getAllJobs() {
        return scheduleService.getAllJobs();
    }

    // @Todo JobDetailName으로 동작
    @PutMapping(value = "/job/pause")
    public ResponseEntity<?> pauseJob(@RequestBody JobRequest jobRequest) {
        JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        if (scheduleService.isJobExists(jobKey)) {
            if (!scheduleService.isJobRunning(jobKey)) {
                scheduleService.pauseJob(jobKey);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, "Job already in running state"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Job does not exits"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Job paused successfully"), HttpStatus.OK);
    }

    // @Todo JobDetailName으로 동작
    @PutMapping(value = "/job/resume")
    public ResponseEntity<?> resumeJob(@RequestBody JobRequest jobRequest) {
        JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        if (scheduleService.isJobExists(jobKey)) {
            String jobState = scheduleService.getJobState(jobKey);

            if (jobState.equals("PAUSED")) {
                scheduleService.resumeJob(jobKey);
            } else {
                return new ResponseEntity<>(new ApiResponse(false, "Job is not in paused state"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Job does not exits"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Job resumed successfully"), HttpStatus.OK);
    }

//    @RequestMapping(value = "/startJob", method = RequestMethod.GET)
//    public boolean startJob() {
//
//        boolean result = scheduleService.testJobs();
//
//        return result;
//    }

}

