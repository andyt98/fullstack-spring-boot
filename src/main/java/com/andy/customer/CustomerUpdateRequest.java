package com.andy.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
