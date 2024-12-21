package com.project.onlybuns.repository;

import com.project.onlybuns.model.Comment;
import com.project.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(Integer postId);
    long countByDateAfter(LocalDateTime date);

    @Query("SELECT COUNT(DISTINCT c.user) FROM Comment c")
    long countDistinctCommentCreators();


}
