package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime start;

    @Column(name = "end_time")  // DB上のカラム名を指定しておく（推奨）
    private LocalDateTime end;

    @Column(length = 1000)
    private String description;

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "target_user_id")
    private Long targetUserId;

    // getter・setter 省略（必要ならLombok使えます）
 // ===== Getter =====
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
    
    public Long getTargetUserId() {
        return targetUserId;
    }

    // ===== Setter =====
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
    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
