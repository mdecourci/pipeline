package com.netpod.flow.batch;

import com.netpod.flow.domain.PaymentFile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

import java.io.File;
import java.util.Date;

public class FileMessageToJobRequest {
    private Job job;
    
    private String fileParameterName = "input.file.name";
    
    public void setFileParameterName(String fileParameterName) {
        this.fileParameterName = fileParameterName;
    }
    
    public void setJob(Job job) {
        this.job = job;
    }
    
    @Transformer
    public JobLaunchRequest toRequest(Message<PaymentFile> message) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        
        jobParametersBuilder.addString(fileParameterName, message.getPayload().getFileName());
        // We add a dummy value to make job params unique, or else spring batch
        // will only run it the first time
        jobParametersBuilder.addDate("dummy", new Date());
        
        return new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());
    }
}
