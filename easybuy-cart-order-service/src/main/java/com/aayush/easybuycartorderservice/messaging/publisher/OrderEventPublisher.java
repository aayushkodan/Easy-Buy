package com.aayush.easybuycartorderservice.messaging.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ORDER_TOPIC = "order-topic";

    public void publishOrderEvent(Object event) {
        kafkaTemplate.send(ORDER_TOPIC, event);
    }
}
