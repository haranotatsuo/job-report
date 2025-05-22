package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CommentDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Event;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setEventId(comment.getEvent().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        return dto;
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId) {
        return commentRepository.findByEventId(eventId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto dto, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "イベントが見つかりません"));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);

        return toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long id, CommentDto dto, String username) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "コメントが見つかりません"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "自分のコメントのみ編集できます");
        }

        comment.setContent(dto.getContent());
        return toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "コメントが見つかりません"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "自分のコメントのみ削除できます");
        }

        commentRepository.delete(comment);
    }
}
