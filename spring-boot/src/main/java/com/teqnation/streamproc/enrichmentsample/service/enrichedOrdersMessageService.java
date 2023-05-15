package com.teqnation.streamproc.enrichmentsample.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.teqnation.streamproc.enrichmentsample.model.EnrichedOrderWithCustomerData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class enrichedOrdersMessageService {

    @Bean
    public PubSubInboundChannelAdapter enrichedOrdersMessageChannelAdapter(
            @Qualifier("enrichedOrdersInputChannel") MessageChannel enrichedOrdersInputChannel,
            PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate, "enrichedOrdersWithCustomersDataTopic");
        adapter.setPayloadType(EnrichedOrderWithCustomerData.class);
        adapter.setOutputChannel(enrichedOrdersInputChannel);

        return adapter;
    }

    @Bean
    public MessageChannel enrichedOrdersInputChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "enrichedOrdersInputChannel")
    public void ordersMessageReceiver(String message) {
        System.out.println(message);
    }
}
