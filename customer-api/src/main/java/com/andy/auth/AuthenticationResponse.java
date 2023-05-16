package com.andy.auth;

import com.andy.customer.CustomerDTO;

public record AuthenticationResponse (
        String token,
        CustomerDTO customerDTO
){
}
