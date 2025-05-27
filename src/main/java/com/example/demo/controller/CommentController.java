package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommentDto;
import com.example.demo.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 指定されたイベントに紐づくコメント一覧を取得
    @GetMapping("/event/{eventId}")
    public List<CommentDto> getComments(@PathVariable Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }
    
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getCommentsAlt(@PathVariable Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }

    // コメント追加（ログインユーザー情報付き）
    @PostMapping
    public CommentDto addComment(
    		@Valid @RequestBody CommentDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.createComment(dto, userDetails.getUsername());
    }

    // コメント更新（自分のコメントのみ可能）
    @PutMapping("/{id}")
    public CommentDto updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.updateComment(id, dto, userDetails.getUsername());
    }

    // コメント削除（自分のコメントのみ可能）
    @DeleteMapping("/{id}")
    public void deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUsername());
    }
}
