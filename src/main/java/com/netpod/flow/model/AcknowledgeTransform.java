package com.netpod.flow.model;

import com.netpod.flow.domain.PaymentFile;
import com.netpod.flow.domain.WorkflowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

public class AcknowledgeTransform {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcknowledgeTransform.class);
    
    @Transformer
    public WorkflowResponse acknowledgementResponse(Message<PaymentFile> message) {
        LOGGER.info("AcknowledgeTransform.acknowledgementResponse = {}", message);
        return new WorkflowResponse(message.toString());
    }
}
