package com.teqnation.streamproc.enrichmentsample.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teqnation.streamproc.enrichmentsample.model.Customer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import static com.teqnation.streamproc.enrichmentsample.config.PubSubConfig.objectMapper;

@Service
public class customersMessageService {

    private final RedisTemplate<String, Customer> redisTemplate;

    public customersMessageService(RedisTemplate<String, Customer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Filter(inputChannel = "customersInputChannel", outputChannel = "filteredCustomersInputChannel")
    boolean filter(Message<String> message) {
        // You can filter some of the messages here
        // List<String> whitelistListOfCities = List.of("New York", "Los Angeles");
        // return whitelistListOfCities.contains(message.getPayload().city());
        return true;
    }

    @ServiceActivator(inputChannel = "filteredCustomersInputChannel")
    public void customersMessageReceiver(String message) throws JsonProcessingException {
        Customer customer = objectMapper().readValue(message, Customer.class);
        System.out.println("Message arrived! Message: " + customer);
        redisTemplate.opsForValue().set(String.valueOf(customer.id()), customer);
    }

}
