package com.example.board.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 카테고리로 게시글 검색 (Post 반환)
    @Query("""
           SELECT DISTINCT p
           FROM Post p
           JOIN p.postCategories pc
           WHERE pc.category.id = :categoryId
           """)
    Page<Post> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // 제목 또는 내용으로 검색
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 사용자별 게시글 조회
    Page<Post> findByUserUsername(String username, Pageable pageable);
}