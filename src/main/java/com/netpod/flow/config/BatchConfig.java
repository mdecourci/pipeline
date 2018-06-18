package com.netpod.flow.config;

import com.netpod.flow.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.function.Function;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    ValidationService validationService;
    
    @Bean
    Step validateStep() {
        return stepBuilderFactory.get("validateStep")//
                .<String, String>chunk(5) //
                .reader(itemReader(null)) //
                .processor((Function<? super String, ? extends String>) validationService::process)
                .writer(i -> i.stream().forEach(j -> LOGGER.info(j))) //
                .build();
    }
    
    @Bean
    Job bpsJob() {
        Job job = jobBuilderFactory.get("bpsJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(validateStep()) //
                .build();
        return job;
    }
    
    @Bean
    @StepScope
    FlatFileItemReader<String> itemReader(@Value("#{jobParameters[file_path]}") String filePath) {
        FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        final FileSystemResource fileResource = new FileSystemResource(filePath);
        reader.setResource(fileResource);
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }
}
