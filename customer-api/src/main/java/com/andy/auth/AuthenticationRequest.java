package com.andy.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
