package com.example.demo.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.demo.dto.EventDto;

public interface EventService {

    @PreAuthorize("hasRole('STAFF')")
    EventDto createEvent(EventDto dto);

    @PreAuthorize("hasRole('STAFF')")
    EventDto updateEvent(Long id, EventDto dto);

    @PreAuthorize("hasAnyRole('STAFF', 'VIEWER')")
    List<EventDto> getAllEvents();

    @PreAuthorize("hasRole('STAFF')")
    void deleteEvent(Long id);
}
