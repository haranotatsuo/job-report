package com.example.demo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EventDto {
    private Long id;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    
    private LocalDateTime end;
    private String description;
    
    private Long targetUserId;

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

    
    public Long getTargetUserId() {
        return targetUserId;
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

    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
