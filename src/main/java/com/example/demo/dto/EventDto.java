package com.example.demo.dto;

import java.time.LocalDateTime;

public class EventDto {
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Long userId;

 // ======= Getter =======

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    // ======= Setter =======

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
