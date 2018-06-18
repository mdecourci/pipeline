package com.netpod.flow.domain;

import org.springframework.integration.dsl.MessageProcessorSpec;

public class PaymentFile {
    private String fileName;
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(final String pFileName) {
        fileName = pFileName;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PaymentFile{");
        sb.append("fileName='").append(fileName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
