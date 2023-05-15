package com.teqnation.streamproc.enrichmentsample.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;


@Configuration
public class PubSubConfig {

    @Value("${pubsub.orders.subscription}")
    private String ORDERS_SUBSCRIPTION;

    @Value("${pubsub.customers.subscription}")
    private String CUSTOMERS_SUBSCRIPTION;

    @Value("${pubsub.enriched-orders.topic}")
    private String ENRICHED_ORDERS_TOPIC;

    @Bean
    public PubSubInboundChannelAdapter ordersMessageChannelAdapter(
            @Qualifier("ordersInputChannel") MessageChannel ordersInputChannel,
            PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate, ORDERS_SUBSCRIPTION);

        adapter.setOutputChannel(ordersInputChannel);

        return adapter;
    }

    @Bean
    public MessageChannel ordersInputChannel() {
        return new DirectChannel();
    }


    @Bean
    public PubSubInboundChannelAdapter customersMessageChannelAdapter(
            @Qualifier("customersInputChannel") MessageChannel customersInputChannel,
            PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate, CUSTOMERS_SUBSCRIPTION);
//        adapter.setAckMode(AckMode.AUTO_ACK);
//        adapter.setPayloadType(Customer.class);
        adapter.setOutputChannel(customersInputChannel);

        return adapter;
    }

//    @Bean
//    @Primary
//    public PubSubMessageConverter pubSubMessageConverter(ObjectMapper objectMapper) {
//        return new JacksonPubSubMessageConverter(objectMapper);
//    }

    //    @Bean
//    @Primary
    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        return mapper;
    }

    @Bean
    public MessageChannel customersInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "outputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        PubSubMessageHandler handler = new PubSubMessageHandler(pubsubTemplate, ENRICHED_ORDERS_TOPIC);
        return handler;
    }

}
