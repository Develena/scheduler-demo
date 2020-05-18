package com.example.scheduler.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobResponse {

    private String jobName;
    private String groupName;
    private String jobStatus;
    private String scheduleTime;
    private String lastFiredTime;
    private String nextFireTime;

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
