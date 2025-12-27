package com.example.board.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class LoginRequest {
    private String username;
    private String password;
}