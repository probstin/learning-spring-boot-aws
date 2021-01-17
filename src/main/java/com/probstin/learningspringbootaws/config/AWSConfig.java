package com.probstin.learningspringbootaws.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    @Value("${aws.accessKey}")
    private String AWS_ACCESS_KEY;

    @Value("${aws.secretKey}")
    private String AWS_SECRET_KEY;

    @Value("${aws.region}")
    private String AWS_REGION;

    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(AWS_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
