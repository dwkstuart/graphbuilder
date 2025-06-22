package com.dwk.enterprise.graphbuilder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "graph.traversal")
public class GraphTraversalConfig {
    
    private int maxTraversalDepth = 1000;
    private int maxGraphSize = 10000;
    private int partitionSizeThreshold = 1000;
    private boolean enablePartitioning = true;
    private int maxPartitions = 10;
    private long traversalTimeoutMs = 30000; // 30 seconds
    
    public int getMaxTraversalDepth() {
        return maxTraversalDepth;
    }
    
    public void setMaxTraversalDepth(int maxTraversalDepth) {
        this.maxTraversalDepth = maxTraversalDepth;
    }
    
    public int getMaxGraphSize() {
        return maxGraphSize;
    }
    
    public void setMaxGraphSize(int maxGraphSize) {
        this.maxGraphSize = maxGraphSize;
    }
    
    public int getPartitionSizeThreshold() {
        return partitionSizeThreshold;
    }
    
    public void setPartitionSizeThreshold(int partitionSizeThreshold) {
        this.partitionSizeThreshold = partitionSizeThreshold;
    }
    
    public boolean isEnablePartitioning() {
        return enablePartitioning;
    }
    
    public void setEnablePartitioning(boolean enablePartitioning) {
        this.enablePartitioning = enablePartitioning;
    }
    
    public int getMaxPartitions() {
        return maxPartitions;
    }
    
    public void setMaxPartitions(int maxPartitions) {
        this.maxPartitions = maxPartitions;
    }
    
    public long getTraversalTimeoutMs() {
        return traversalTimeoutMs;
    }
    
    public void setTraversalTimeoutMs(long traversalTimeoutMs) {
        this.traversalTimeoutMs = traversalTimeoutMs;
    }
} 