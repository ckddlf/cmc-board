package com.example.board.authorization;

import com.example.board.domain.user.User;

public interface AuthorizationStrategy {
    boolean hasPermission(User user, Object resource);
}