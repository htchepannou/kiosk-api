package io.tchepannou.kiosk.api.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConfigurationProperties("kiosk.aws")
public class AwsConfiguration {
    @Autowired
    Environment env;

    private int connectionTimeout;
    private int maxErrorRetries;

    //-- Beans
    @Bean
    AmazonS3 amazonS3() {
        return new AmazonS3Client(awsCredentialsProvider(), awsClientConfiguration());
    }

    @Bean
    AmazonSQS amazonSQS() {
        return new AmazonSQSClient(awsCredentialsProvider(), awsClientConfiguration());
    }

    @Bean
    AWSCredentialsProvider awsCredentialsProvider() {
        if (env.acceptsProfiles("ci")) {
            return new SystemPropertiesCredentialsProvider();
        } else if (env.acceptsProfiles("dev")) {
            final String home = System.getProperty("user.home");
            return new PropertiesFileCredentialsProvider(home + "/.aws/credentials");
        } else {
            return new DefaultAWSCredentialsProviderChain();
        }
    }

    @Bean
    ClientConfiguration awsClientConfiguration() {
        return new ClientConfiguration()
                .withConnectionTimeout(connectionTimeout)
                .withGzip(true)
                .withMaxErrorRetry(maxErrorRetries)
                ;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxErrorRetries() {
        return maxErrorRetries;
    }

    public void setMaxErrorRetries(final int maxErrorRetries) {
        this.maxErrorRetries = maxErrorRetries;
    }
}
