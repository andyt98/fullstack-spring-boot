package com.andy.customer;

public record CustomerRegistrationRequest (
        String name,
        String email,
        Integer age
){
}
