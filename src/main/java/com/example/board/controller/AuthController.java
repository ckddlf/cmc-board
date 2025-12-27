package com.example.board.controller;

import com.example.board.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")  // /api/auth로 변경하여 충돌 방지
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestParam String username,
                                        @RequestParam String password) {
        authService.signup(username, password);
        return ResponseEntity.ok("회원가입 성공");
    }
}