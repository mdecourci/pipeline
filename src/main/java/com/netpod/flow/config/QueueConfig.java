package com.netpod.flow.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@Configuration
@EnableJms
@Import(ActiveMQAutoConfiguration.class)
public class QueueConfig {
    
    public static final String INPUT_QUEUE = "payment.file.input";
    public static final String OUTPUT_QUEUE = "payment.file.output";
    
    @Bean
    public Queue gatewayInputQueue() {
        return new ActiveMQQueue(INPUT_QUEUE);
    }
    
    @Bean
    public Queue outputQueue() {
        return new ActiveMQQueue(OUTPUT_QUEUE);
    }
}
