package com.netpod.flow.model;

import com.netpod.flow.domain.PaymentFile;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

@MessagingGateway
public interface PaymentGateway {
    @Gateway(requestChannel = "gatewayChannel")
    void sendFile(PaymentFile pPaymentFile);
    
}
