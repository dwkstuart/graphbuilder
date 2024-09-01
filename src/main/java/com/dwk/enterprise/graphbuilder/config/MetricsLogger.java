package com.dwk.enterprise.graphbuilder.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsLogger {

    private static final Logger logger = LoggerFactory.getLogger(MetricsLogger.class);
    private final MeterRegistry meterRegistry;
    private final CloudWatchMetricsService metricsService;


    public MetricsLogger(MeterRegistry meterRegistry, CloudWatchMetricsService metricsService) {
        this.meterRegistry = meterRegistry;
        this.metricsService = metricsService;
    }


    @Scheduled(fixedRate = 60000) // Log every 60 seconds
    public void logJvmMetrics() {
        meterRegistry.getMeters().forEach(meter -> {
            meter.measure().forEach(measurement -> {
                metricsService.publishCustomMetric("CustomMetricName", 1.0, "YourNamespace");

                logger.info("Metric: {} - Value: {}", meter.getId(), measurement.getValue());
            });
        });
    }
}
