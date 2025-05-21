package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.EventDto;
import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    private EventDto toDto(Event entity) {
        EventDto dto = new EventDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setStart(entity.getStart());
        dto.setEndTime(entity.getEnd());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    private Event toEntity(EventDto dto) {
        Event e = new Event();
        e.setId(dto.getId());
        e.setTitle(dto.getTitle());
        e.setStart(dto.getStart());
        e.setEnd(dto.getEndTime());
        e.setDescription(dto.getDescription());
        e.setUserId(dto.getUserId());
        return e;
    }

    @Override
    public EventDto createEvent(EventDto dto) {
        Event saved = eventRepository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public EventDto updateEvent(Long id, EventDto dto) {
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "指定されたイベントが見つかりません: ID = " + id));

        existing.setTitle(dto.getTitle());
        existing.setStart(dto.getStart());
        existing.setEnd(dto.getEndTime());
        existing.setDescription(dto.getDescription());
        existing.setUserId(dto.getUserId()); // 念のため追加（必要に応じて）
        
        return toDto(eventRepository.save(existing));
    }


    @Override
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
