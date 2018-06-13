package com.netpod.flow.config;

import com.netpod.flow.model.WorkFlowRouter;
import com.netpod.flow.domain.PaymentFile;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Configuration
public class IntegrationFlowConfig {
    
    
    @Autowired
    protected JmsTemplate jmsTemplate;
    
    @Autowired
    protected Queue outputQueue;
    
    @Bean
    protected WorkFlowRouter workFlowRouter() {
        return new WorkFlowRouter();
    }
    
    @Bean
    public MessageChannel gatewayChannel() {
        return MessageChannels.direct("gatewayInputQueue").get();
    }
    
    @Bean
    public MessageChannel queueChannel() {
        return MessageChannels.publishSubscribe("outputQueue").get();
    }
    
    @Bean
    public IntegrationFlow payments() {
        //Jms.inboundGateway(new ActiveMQConnectionFactory()).destination()
    
        
        //Jms.outboundAdapter(jmsTemplate).destination(outputQueue)
        return f -> f.log(LoggingHandler.Level.DEBUG).channel(gatewayChannel())
                .route(PaymentFile.class,
                        p -> workFlowRouter().loadWorkFlow(p),
                        mapping -> mapping.subFlowMapping("workFlow1", workSubFlow1())
                                .subFlowMapping("workFlow2", workSubFlow2()))
                .channel(queueChannel());
    }
    
    protected IntegrationFlow workSubFlow2() {
        return f -> f.transform(p -> "In workSubFlow1 " + p.toString());
//        return f -> f.transform(p -> "In workSubFlow1 " + p.toString()).route(PaymentFile.class, p -> p != null, mapping -> mapping.channelMapping(true, "queueChannel"));
    }
    
    protected IntegrationFlow workSubFlow1() {
        return f -> f.transform(p -> "In workSubFlow1 " + p.toString());
    }
}
