package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.EventDto;
import com.example.demo.model.Event;
import com.example.demo.model.User;
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
        dto.setEnd(entity.getEnd());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUserId()); // 表示用には必要
        dto.setTargetUserId(entity.getTargetUserId());
        return dto;
    }

    private Event toEntity(EventDto dto) {
        Event e = new Event();
        e.setId(dto.getId());
        e.setTitle(dto.getTitle());
        e.setStart(dto.getStart());
        e.setEnd(dto.getEnd());
        e.setDescription(dto.getDescription());
        e.setUserId(dto.getUserId());
        e.setTargetUserId(dto.getTargetUserId());
        return e;
    }
    

    @Override
    public EventDto createEventForUser(EventDto dto, User user) {
        Event entity = toEntity(dto);
     // 作成者をログインユーザーに固定（不正操作防止）
        entity.setUserId(user.getId()); // 安全な方法で設定
     // targetUserId が null の場合は、自分自身を対象とする
        if (entity.getTargetUserId() == null) {
            entity.setTargetUserId(user.getId());
        }
        
        Event saved = eventRepository.save(entity);
        return toDto(saved);
    }

    @Override
    public EventDto updateEvent(Long id, EventDto dto) {
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "指定されたイベントが見つかりません: ID = " + id));

        existing.setTitle(dto.getTitle());
        existing.setStart(dto.getStart());
        existing.setEnd(dto.getEnd());
        existing.setDescription(dto.getDescription());
        // userId の更新は不要・禁止（セキュリティ的に）

        return toDto(eventRepository.save(existing));
    }

    @Override
    public List<EventDto> getEventsByUserId(Long userId) {
        return eventRepository.findByTargetUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
