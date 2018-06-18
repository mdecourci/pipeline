package com.netpod.flow;

import com.netpod.flow.config.IntegrationFlowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    
    public String process(String msg) {
        LOGGER.info("ValidationService.process()");
        return msg;
    }
}
