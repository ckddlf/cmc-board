package com.example.board.service;

import com.example.board.domain.user.Role;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    public AuthServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원가입_성공() {
        String username = "user1";
        String password = "pw";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPw");

        authService.signup(username, password);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void 회원가입_실패_중복사용자() {
        String username = "user1";
        String password = "pw";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(new User(username, "pw", Role.USER)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.signup(username, password)
        );

        assertEquals("이미 존재하는 사용자", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}