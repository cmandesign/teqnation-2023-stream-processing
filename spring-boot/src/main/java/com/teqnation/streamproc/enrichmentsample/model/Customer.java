package com.teqnation.streamproc.enrichmentsample.model;
public record Customer(
        long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String city
) {}

