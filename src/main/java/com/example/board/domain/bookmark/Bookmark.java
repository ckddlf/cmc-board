package com.example.board.domain.bookmark;

import com.example.board.domain.post.Post;
import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;   
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "post_id"}
    )
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Bookmark(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}