package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.entity.Notification;
import com.careconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return notificationService.subscribe();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getByUser(userId)));
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@PathVariable Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }
}
