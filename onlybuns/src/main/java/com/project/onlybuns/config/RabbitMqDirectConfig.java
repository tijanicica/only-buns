package com.project.onlybuns.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqDirectConfig {
    public static final String QUEUE_NAME = "rabbit-care-queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }
}
