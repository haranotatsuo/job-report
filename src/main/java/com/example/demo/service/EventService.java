package com.example.demo.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.demo.dto.EventDto;
import com.example.demo.model.User;

public interface EventService {

    @PreAuthorize("hasRole('STAFF')")
    EventDto createEventForUser(EventDto dto, User user);

    @PreAuthorize("hasRole('STAFF')")
    EventDto updateEvent(Long id, EventDto dto);

    @PreAuthorize("hasAnyRole('STAFF', 'USER')")
    List<EventDto> getEventsByUserId(Long userId);

    @PreAuthorize("hasRole('STAFF')")
    void deleteEvent(Long id);
}
