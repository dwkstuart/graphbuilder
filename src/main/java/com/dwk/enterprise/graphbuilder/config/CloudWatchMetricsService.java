package com.dwk.enterprise.graphbuilder.config;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.net.URI;
import java.time.Instant;

@Service
public class CloudWatchMetricsService {

    private final CloudWatchClient cloudWatchClient;

    public CloudWatchMetricsService() {
        this.cloudWatchClient = CloudWatchClient.builder().endpointOverride(URI.create("http://localhost:4566")).build();
    }

    public void publishCustomMetric(String metricName, double value, String namespace) {
        MetricDatum datum = MetricDatum.builder()
                .metricName(metricName)
                .timestamp(Instant.now())
                .value(value)
                .unit(StandardUnit.NONE)
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace(namespace)
                .metricData(datum)
                .build();

        cloudWatchClient.putMetricData(request);
    }
}
