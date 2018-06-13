package com.netpod.flow.config;

import com.netpod.Application;
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static com.netpod.flow.config.QueueConfig.OUTPUT_QUEUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({IntegrationFlowConfig.class, QueueConfig.class})
public class IntegrationFlowConfigTest {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    PaymentGateway paymentGateway;
    
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void shouldProcessFlow() throws Exception {
    
      //  outputCapture.expect(containsString("In workSubFlow2"));
        
        this.paymentGateway.sendFile(new PaymentFile());

        this.jmsTemplate.setReceiveTimeout(10_000);
    
        String message = (String) this.jmsTemplate.receiveAndConvert(OUTPUT_QUEUE);

        assertThat(message, is("workFlow2"));
    }
    
}