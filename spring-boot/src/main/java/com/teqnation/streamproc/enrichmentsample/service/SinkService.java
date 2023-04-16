package com.teqnation.streamproc.enrichmentsample.service;

import com.teqnation.streamproc.enrichmentsample.model.EnrichedOrderWithCustomerData;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "outputChannel")
public interface SinkService {

    void publish(EnrichedOrderWithCustomerData enrichedOrderWithCustomerData);

}
