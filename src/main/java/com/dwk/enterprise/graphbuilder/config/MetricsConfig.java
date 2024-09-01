package com.dwk.enterprise.graphbuilder.config;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

//    @Bean
//    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
//        return registry -> {
////            new ClassLoaderMetrics().bindTo(registry);
////            new JvmMemoryMetrics().bindTo(registry);
//            new JvmGcMetrics().bindTo(registry);
////            new JvmThreadMetrics().bindTo(registry);
//        };
//    }


    @Bean
    public MeterFilter customMetricFilter() {
        return MeterFilter.denyUnless(id -> id.getName().startsWith("jvm.gc"));
    }
}
