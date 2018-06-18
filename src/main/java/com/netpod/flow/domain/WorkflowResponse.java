package com.netpod.flow.domain;

import org.springframework.batch.core.JobExecution;

public class WorkflowResponse {
    private JobExecution jobExecution;
    private String message;
    
    public WorkflowResponse(final JobExecution pJobExecution) {
        jobExecution = pJobExecution;
    }
    
    public WorkflowResponse(final String pMessage) {
        message = pMessage;
    }
    
    public JobExecution getJobExecution() {
        return jobExecution;
    }
    
    public void setJobExecution(final JobExecution pJobExecution) {
        jobExecution = pJobExecution;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(final String pMessage) {
        message = pMessage;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkflowResponse{");
        sb.append("jobExecution=").append(jobExecution);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
