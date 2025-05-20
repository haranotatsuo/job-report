package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.EventDto;

public interface EventService {
    EventDto createEvent(EventDto dto);
    EventDto updateEvent(Long id, EventDto dto);
    List<EventDto> getAllEvents();
    void deleteEvent(Long id);
}
