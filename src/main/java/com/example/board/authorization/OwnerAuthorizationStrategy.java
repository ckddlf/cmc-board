package com.example.board.authorization;

import com.example.board.domain.comment.Comment;
import com.example.board.domain.post.Post;
import com.example.board.domain.user.User;

public class OwnerAuthorizationStrategy implements AuthorizationStrategy {

    @Override
    public boolean hasPermission(User user, Object resource) {
        if (resource instanceof Post post) {
            return post.getUser().equals(user);
        }
        if (resource instanceof Comment comment) {
            return comment.getUser().equals(user);
        }
        return false;
    }
}