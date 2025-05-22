// CommentService.java
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CommentDto;

public interface CommentService {
    List<CommentDto> getCommentsByEventId(Long eventId);
    CommentDto createComment(CommentDto dto, String username);
    CommentDto updateComment(Long id, CommentDto dto, String username);
    void deleteComment(Long id, String username);
}
