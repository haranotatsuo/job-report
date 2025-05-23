package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EventDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    // ✅ ログインユーザーのイベント取得
    @GetMapping
    public List<EventDto> getUserEvents(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));
        
        if ("ROLE_STAFF".equals(user.getRole())) {
            // スタッフ → 全イベントを取得
            return eventService.getAllEvents();
        } else {
            // 利用者 → 自分が対象のイベントのみ取得
            return eventService.getEventsByTargetUserId(user.getId());
        }
    }

    // ✅ ログインユーザーでイベント登録
    @PostMapping
    public EventDto createEvent(@RequestBody EventDto dto,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));
        return eventService.createEventForUser(dto, user);
    }

    @PutMapping("/{id}")
    public EventDto updateEvent(@PathVariable Long id,
                                @RequestBody EventDto dto,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));
        return eventService.updateEvent(id, dto, user);
    }


    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
