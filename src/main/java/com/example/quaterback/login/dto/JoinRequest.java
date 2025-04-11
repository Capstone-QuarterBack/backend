package com.example.quaterback.login.dto;

public record JoinRequest(
        String username,
        String password
) {
    public static JoinRequest of(String username, String password) {
        return new JoinRequest(username, password);
    }
}
