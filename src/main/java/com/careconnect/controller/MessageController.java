package com.careconnect.controller;

import com.careconnect.dto.request.MessageRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.MessageResponse;
import com.careconnect.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> send(
            @RequestParam Long senderId,
            @RequestParam String senderName,
            @RequestParam String senderRole,
            @RequestBody MessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                messageService.send(senderId, senderName, senderRole, request)));
    }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> conversation(
            @RequestParam Long user1,
            @RequestParam Long user2) {
        return ResponseEntity.ok(ApiResponse.success(
                messageService.getConversation(user1, user2)));
    }

    @PatchMapping("/read")
    public ResponseEntity<Void> markRead(
            @RequestParam Long userId,
            @RequestParam Long senderId) {
        messageService.markRead(userId, senderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-senders")
    public ResponseEntity<ApiResponse<java.util.List<Long>>> unreadSenders(
            @RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(messageService.getUnreadSenders(userId)));
    }
}
