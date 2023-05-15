package com.teqnation.streamproc.enrichmentsample.service;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "outputChannel")
public interface SinkService {

    void publish(String enrichedOrderWithCustomerData);

}
