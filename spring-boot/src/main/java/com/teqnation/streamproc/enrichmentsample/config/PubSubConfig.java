package com.teqnation.streamproc.enrichmentsample.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.teqnation.streamproc.enrichmentsample.model.Customer;
import com.teqnation.streamproc.enrichmentsample.model.EnrichedOrderWithCustomerData;
import com.teqnation.streamproc.enrichmentsample.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;


@Configuration
public class PubSubConfig {

    @Bean
    public PubSubInboundChannelAdapter ordersMessageChannelAdapter(
            @Qualifier("ordersInputChannel") MessageChannel ordersInputChannel,
            PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate, "orders");
        adapter.setPayloadType(Order.class);
        adapter.setOutputChannel(ordersInputChannel);

        return adapter;
    }

    @Bean
    public MessageChannel ordersInputChannel() {
        return new DirectChannel();
    }


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

    @Bean
    public PubSubInboundChannelAdapter customersMessageChannelAdapter(
            @Qualifier("customersInputChannel") MessageChannel customersInputChannel,
            PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate, "customers");
        adapter.setPayloadType(Customer.class);
        adapter.setOutputChannel(customersInputChannel);

        return adapter;
    }

    @Bean
    @Primary
    public PubSubMessageConverter pubSubMessageConverter(ObjectMapper objectMapper) {
        return new JacksonPubSubMessageConverter(objectMapper);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Bean
    public MessageChannel customersInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "outputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        PubSubMessageHandler handler = new PubSubMessageHandler(pubsubTemplate, "enrichedOrdersWithCustomersDataTopic");
        return handler;
    }

}
