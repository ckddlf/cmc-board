package com.example.board.service;

import com.example.board.authorization.AuthorizationStrategy;
import com.example.board.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

@Service
public class AuthorizationService {

    private final Map<String, AuthorizationStrategy> strategies;

    public AuthorizationService(Map<String, AuthorizationStrategy> strategies) {
        this.strategies = strategies;
    }

    public void checkPermission(User user, Object resource, String action) {
        AuthorizationStrategy strategy = strategies.get(action);
        if (strategy == null || !strategy.hasPermission(user, resource)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }
}