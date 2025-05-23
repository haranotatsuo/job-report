package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.EventDto;
import com.example.demo.model.User;

public interface EventService {

	// 利用者・スタッフ共に使用可能（表示）
    List<EventDto> getEventsByTargetUserId(Long userId);

    // スタッフのみ → 全イベント取得（管理者ビュー用）
    List<EventDto> getAllEvents();

    // スタッフのみ → イベント登録
    EventDto createEventForUser(EventDto dto, User user);

    // スタッフのみ → イベント更新
    EventDto updateEvent(Long id, EventDto dto, User currentUser);

    // スタッフのみ → イベント削除
    void deleteEvent(Long id);
    
    
}
