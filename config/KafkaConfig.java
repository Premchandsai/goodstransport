package com.p2p.transport.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic requestTopic() {
        return TopicBuilder.name("transport-requests")
                .partitions(10)
                .replicas(1) // Changed from 2 to 1
                .build();
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notifications")
                .partitions(10)
                .replicas(1) // Changed from 2 to 1
                .build();
    }
}