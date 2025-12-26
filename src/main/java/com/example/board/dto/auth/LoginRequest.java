package com.example.board.dto.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String username;
    private String password;
}