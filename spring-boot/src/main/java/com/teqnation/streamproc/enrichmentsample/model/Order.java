package com.teqnation.streamproc.enrichmentsample.model;

import java.util.List;

public record Order(
        long id,
        long customerId,
        String orderDate,
        List<OrderItem> items,
        OrderStatus status
) {

    public record OrderItem(
            long id,
            long productId,
            int quantity,
            double price
    ) {
    }

    public enum OrderStatus {
        CREATED,
        PROCESSING,
        COMPLETED,
        CANCELED
    }

}


