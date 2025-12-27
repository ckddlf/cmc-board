package com.example.board.service;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.CategoryRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Long create(String username, String title, String content, List<Long> categoryIds) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = new Post(title, content, user);
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            post.addCategory(category);
        }
        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void update(Long postId, String username, 
                       String title, String content,List<Long> categoryIds) {

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 글만 수정 가능");
        }

        post.update(title, content);

        post.clearCategories();

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            post.addCategory(category);
        }
    }

    public void delete(Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow();

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 글만 삭제 가능");
        }

        postRepository.delete(post);
    }
}