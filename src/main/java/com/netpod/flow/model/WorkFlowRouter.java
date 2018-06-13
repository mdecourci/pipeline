package com.netpod.flow.model;

import com.netpod.flow.domain.PaymentFile;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Component;

public class WorkFlowRouter {
    
    @Router
    public String loadWorkFlow(PaymentFile pPaymentFile) {
        return "workFlow2";
    }
}
