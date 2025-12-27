package com.example.board.domain.post;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.PostCategory;
import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void addCategory(Category category) {
        postCategories.add(new PostCategory(this, category));
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void clearCategories() {
        postCategories.clear();
    }
}