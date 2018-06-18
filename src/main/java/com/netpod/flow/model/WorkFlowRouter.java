package com.netpod.flow.model;

import com.netpod.flow.BpsFlowType;
import com.netpod.flow.domain.PaymentFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Component;

public class WorkFlowRouter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowRouter.class);
    
    @Router
    public String loadWorkFlow(PaymentFile pPaymentFile) {
        LOGGER.info("!!! WorkFlowRouter.loadWorkFlow pPaymentFile = " + pPaymentFile);
        //File workflow =
        return BpsFlowType.VALIDATE_PAYMENT.toString();
//        return BpsFlowType.ACKNOWLEDGE.toString();
    }
}
