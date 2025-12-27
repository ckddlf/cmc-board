package com.example.board.domain.post;

import com.example.board.domain.comment.Comment;
import com.example.board.domain.category.Category;
import com.example.board.domain.category.PostCategory;
import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCategory> postCategories = new ArrayList<>();

    // 생성일, 수정일 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }
    
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        for (PostCategory pc : postCategories) {
            categories.add(pc.getCategory());
        }
        return categories;
    }
    
    public void addCategory(Category category) {
        postCategories.add(new PostCategory(this, category));
    }
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void clearCategories() {
        postCategories.clear();
    }

    // 엔티티가 처음 저장될 때 실행
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 수정될 때 실행
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}