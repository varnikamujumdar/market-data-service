package com.example.marketdataservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic priceEventsTopic() {
        return TopicBuilder.name("price-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
