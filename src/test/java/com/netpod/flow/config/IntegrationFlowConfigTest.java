package com.netpod.flow.config;

import com.netpod.Application;
import com.netpod.flow.BpsFlowType;
import com.netpod.flow.domain.PaymentFile;
import com.netpod.flow.model.PaymentGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Message;
import javax.jms.Queue;

import static com.netpod.flow.config.QueueConfig.OUTPUT_QUEUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
@Import({IntegrationFlowConfig.class, QueueConfig.class, BatchConfig.class})
public class IntegrationFlowConfigTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    PaymentGateway paymentGateway;
    
    @Autowired
    protected Queue outputQueue;
    
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void shouldProcessFlow() throws Exception {
    
//        outputCapture.expect(containsString("In acknowledgeSubFlow"));
        
        PaymentFile paymentFile = new PaymentFile();
        paymentFile.setFileName("src/test/resources/sample.txt");
        
        this.paymentGateway.sendFile(paymentFile);
        
        this.jmsTemplate.setReceiveTimeout(10_000);
    
        Message message = this.jmsTemplate.receive(outputQueue);

        assertThat(message, is(BpsFlowType.VALIDATE_PAYMENT.toString()));
    }
    
}