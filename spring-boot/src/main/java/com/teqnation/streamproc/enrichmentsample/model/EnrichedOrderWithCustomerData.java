package com.teqnation.streamproc.enrichmentsample.model;

public record EnrichedOrderWithCustomerData(
        Customer customer,
        Order order
) {
}
