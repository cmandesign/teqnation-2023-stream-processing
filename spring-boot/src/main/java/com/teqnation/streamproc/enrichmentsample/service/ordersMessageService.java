package com.teqnation.streamproc.enrichmentsample.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teqnation.streamproc.enrichmentsample.model.Customer;
import com.teqnation.streamproc.enrichmentsample.model.EnrichedOrderWithCustomerData;
import com.teqnation.streamproc.enrichmentsample.model.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import static com.teqnation.streamproc.enrichmentsample.config.PubSubConfig.objectMapper;

@Service
public class ordersMessageService {

    private final RedisTemplate<String, Customer> redisTemplate;
    private final SinkService sinkService;

    public ordersMessageService(RedisTemplate<String, Customer> redisTemplate, SinkService sinkService) {
        this.redisTemplate = redisTemplate;
        this.sinkService = sinkService;
    }

    @ServiceActivator(inputChannel = "ordersInputChannel")
    public void ordersMessageReceiver(String message) throws JsonProcessingException {
        Order order = objectMapper().readValue(message, Order.class);
        System.out.println("message arrived " + order.toString());
        Customer customer = redisTemplate.opsForValue().get(String.valueOf(order.customerId()));
        EnrichedOrderWithCustomerData enrichedObject = new EnrichedOrderWithCustomerData(customer, order);
        String enriched = objectMapper().writeValueAsString(enrichedObject);
        sinkService.publish(enriched);
    }
}
