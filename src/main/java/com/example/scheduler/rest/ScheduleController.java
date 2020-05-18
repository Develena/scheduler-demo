package com.example.scheduler.rest;

import com.example.scheduler.entity.JobRequest;
import com.example.scheduler.entity.JobStatusResponse;
import com.example.scheduler.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/scheduler")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

//    @RequestMapping(value = "/job", method = RequestMethod.POST)
//    public ResponseEntity<?> addScheduleJob(@ModelAttribute JobRequest jobRequest) {
//        log.debug("add schedule job :: jobRequest : {}", jobRequest.toString());
//
//
//    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    public String getAllJobs() {
//        return scheduleService.getAllJobs();
        return "success";
    }

    @RequestMapping(value = "/startJob", method = RequestMethod.GET)
    public String startJob() {

        String result = scheduleService.testJobs();

        return result;
    }

}

