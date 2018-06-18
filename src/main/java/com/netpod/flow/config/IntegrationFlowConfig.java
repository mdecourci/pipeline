package com.netpod.flow.config;

import com.netpod.flow.batch.FileMessageToJobRequest;
import com.netpod.flow.domain.WorkflowResponse;
import com.netpod.flow.model.AcknowledgeTransform;
import com.netpod.flow.model.WorkFlowRouter;
import com.netpod.flow.domain.PaymentFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.integration.jms.PollableJmsChannel;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.GenericEndpointSpec;

import javax.jms.Queue;

import java.io.File;
import java.util.function.Consumer;

import static com.netpod.flow.BpsFlowType.*;
import static com.netpod.flow.config.QueueConfig.OUTPUT_QUEUE;

@Configuration
@EnableIntegration
@EnableJms
@Import(QueueConfig.class)
public class IntegrationFlowConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationFlowConfig.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job bpsJob;
    
    @Autowired
    protected JmsTemplate jmsTemplate;
    
    @Autowired
    protected Queue outputQueue;
    
    @Autowired
    public Queue gatewayInputQueue;
    
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
        return new QueueChannel();
    }
    
    protected MessageChannel someChannel() {
        return new PublishSubscribeChannel();
    }
    
//    protected MessageChannel jmsChannel() {
//        Jms.outboundAdapter(jmsTemplate).destination();
//        return MessageChannels.queue("outputQueue").get();
//    }
    
    @Bean
    public IntegrationFlow payments() {
        return f -> f.wireTap(w -> w.log(LoggingHandler.Level.DEBUG)
                .channel(gatewayChannel())
                .route(PaymentFile.class,
                        p -> workFlowRouter().loadWorkFlow(p),
                        mapping -> mapping.subFlowMapping(VALIDATE_PAYMENT.toString(), validatePaymentSubFlow())
                                .subFlowMapping(ACKNOWLEDGE.toString(), acknowledgeSubFlow()))
                .transform(p -> {
                    LOGGER.info("last handle = {}, type = {}", p, p.getClass().getName());
                    return p;
                })
                .channel(queueChannel())
                .handle(jmsSendingMessageHandler(),
                        new Consumer<GenericEndpointSpec<JmsSendingMessageHandler>>() {
                            @Override
                            public void accept(final GenericEndpointSpec<JmsSendingMessageHandler> spec) {
                                LOGGER.info("Pooler = {}", spec.getObjectType());
                                spec.poller(Pollers.fixedDelay(10000)
                                        .receiveTimeout(0));
    
                            }
                        }));
                //.channel(queueChannel());
    }
    
    
    protected DirectChannel jobInputChannel() {
        return new DirectChannel();
    }
    
    
    protected IntegrationFlow validatePaymentSubFlow() {
//        return IntegrationFlows.from(this::fileReadingMessageSource, c -> c.poller(Pollers.fixedDelay(5000)))
//                .channel(jobInputChannel())
        return f ->
                f.transform(fileMessageToJobRequest())
                .handle(jobLaunchingMessageHandler())
                        .channel(jobInputChannel())
                .transform((JobExecution jobExecution) -> {
                    LOGGER.info("Handled job = {}, type = {}", jobExecution.toString(), jobExecution.getClass().getName());
                    return new WorkflowResponse(jobExecution);
                });
        
//        return f -> f.transform(p -> {
//            LOGGER.info("*** In validatePaymentSubFlow");
//            return "In validatePaymentSubFlow " + p.toString();
//        });
    }
    
    protected IntegrationFlow acknowledgeSubFlow() {
        return f -> f.transform(acknowledgeTransform());
//        return f -> f.transform(p -> {
//            LOGGER.info("*** In acknowledgeSubFlow");
//            return "In acknowledgeSubFlow " + p.toString();
//        });
    }
    
    private AcknowledgeTransform acknowledgeTransform() {
        AcknowledgeTransform transformer = new AcknowledgeTransform();
        return transformer;
    }
    
    @Bean
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File("dropfolder"));
        source.setFilter(new SimplePatternFileListFilter("*.txt"));
        source.setUseWatchService(true);
        source.setWatchEvents(FileReadingMessageSource.WatchEventType.CREATE);
        return source;
    }
    
    @Bean
    @ServiceActivator(inputChannel = "queueChannel", poller = @Poller(fixedDelay = "1000"))
    JmsSendingMessageHandler jmsSendingMessageHandler() {
        LOGGER.info("jmsSendingMessageHandler --");
        JmsSendingMessageHandler handler = new JmsSendingMessageHandler(jmsTemplate);
        handler.setDestination(outputQueue);
        return handler;
    }
    
    @Bean
    JobLaunchingMessageHandler jobLaunchingMessageHandler() {
        JobLaunchingMessageHandler handler = new JobLaunchingMessageHandler(jobLauncher);
        return handler;
    }
    
    @Bean
    FileMessageToJobRequest fileMessageToJobRequest() {
        FileMessageToJobRequest transformer = new FileMessageToJobRequest();
        transformer.setJob(bpsJob);
        transformer.setFileParameterName("file_path");
        return transformer;
    }
}
