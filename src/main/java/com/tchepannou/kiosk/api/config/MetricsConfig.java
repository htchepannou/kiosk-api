package com.tchepannou.kiosk.api.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MetricsConfig {
    @Bean
    MetricRegistry localMetricRegistry() {
        final MetricRegistry metrics = new MetricRegistry();

        final JmxReporter jmx = JmxReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        jmx.start();

        return metrics;
    }

}
