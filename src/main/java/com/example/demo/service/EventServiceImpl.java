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
import com.example.demo.repository.UserRepository;
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    
    private EventDto toDto(Event entity) {
        EventDto dto = new EventDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setStart(entity.getStart());
        dto.setEnd(entity.getEnd());
        dto.setDescription(entity.getDescription());
        dto.setTargetUserId(entity.getTargetUserId());
        userRepository.findById(entity.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername()); // ← 担当者の名前（usernameなど）
        });
        return dto;
    }

    private Event toEntity(EventDto dto) {
        Event e = new Event();
        e.setId(dto.getId());
        e.setTitle(dto.getTitle());
        e.setStart(dto.getStart());
        e.setEnd(dto.getEnd());
        e.setDescription(dto.getDescription());
        
        e.setTargetUserId(dto.getTargetUserId());
        return e;
    }
    

    @Override
    public EventDto createEventForUser(EventDto dto, User user) {
    	// ⭐ 対象ユーザーが選ばれていない場合はエラー
        if (dto.getTargetUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "対象ユーザーを選択してください。");
        }
        
     // ⭐ 宛先ユーザーが実在するかチェック
        User targetUser = userRepository.findById(dto.getTargetUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "指定された宛先ユーザーが存在しません"));
        
        Event entity = toEntity(dto);
     // 作成者をログインユーザーに固定（不正操作防止）
        entity.setUserId(user.getId()); // 記録した人（スタッフなど）
     
        entity.setTargetUserId(targetUser.getId()); // 宛先ユーザーID（誰の記録か）
        
        Event saved = eventRepository.save(entity);
        return toDto(saved);
    }

    @Override
    public EventDto updateEvent(Long id, EventDto dto, User currentUser) {
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "指定されたイベントが見つかりません: ID = " + id));

        // 自分のイベント or スタッフならOK
        boolean isOwner = existing.getUserId().equals(currentUser.getId());
        boolean isStaff = "ROLE_STAFF".equals(currentUser.getRole());

        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "他人のイベントを編集する権限がありません");
        }

        existing.setTitle(dto.getTitle());
        existing.setStart(dto.getStart());
        existing.setEnd(dto.getEnd());
        existing.setDescription(dto.getDescription());

        // ⭐ スタッフが対象ユーザーを変更する場合（任意）
        if (isStaff && dto.getTargetUserId() != null) {
            User newTarget = userRepository.findById(dto.getTargetUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "指定された対象ユーザーが存在しません"));
            existing.setTargetUserId(newTarget.getId());
        }

        return toDto(eventRepository.save(existing));
    }


    public List<EventDto> getEventsForCurrentUser(User user) {
        // 利用者 → 自分の targetUserId のみ
        if (user.getRole().equals("ROLE_USER")) {
            return eventRepository.findByTargetUserId(user.getId())
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        // スタッフ → 全イベント
        return eventRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getEventsByTargetUserId(Long targetUserId) {
        return eventRepository.findByTargetUserId(targetUserId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
