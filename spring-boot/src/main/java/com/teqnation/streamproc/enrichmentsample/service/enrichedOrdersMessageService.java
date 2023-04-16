package com.teqnation.streamproc.enrichmentsample.service;

import com.teqnation.streamproc.enrichmentsample.model.Customer;
import com.teqnation.streamproc.enrichmentsample.model.EnrichedOrderWithCustomerData;
import com.teqnation.streamproc.enrichmentsample.model.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

@Service
public class enrichedOrdersMessageService {

    @ServiceActivator(inputChannel = "enrichedOrdersInputChannel")
    public void ordersMessageReceiver(String message) {
        System.out.println(message);
    }
}
