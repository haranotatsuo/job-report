// CommentRepository.java
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Comment;
import com.example.demo.model.Event;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId);
    List<Comment> findByParentComment(Comment parent); // 返信取得
    List<Comment> findByEventAndParentCommentIsNull(Event event); // 親コメント一覧取得
}
