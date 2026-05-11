package com.careconnect.service;

import com.careconnect.dto.request.MessageRequest;
import com.careconnect.dto.response.MessageResponse;
import com.careconnect.entity.Message;
import com.careconnect.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    @Transactional
    public MessageResponse send(Long senderId, String senderName, String senderRole, MessageRequest request) {
        Message msg = Message.builder()
                .senderId(senderId)
                .senderName(senderName)
                .senderRole(senderRole)
                .receiverId(request.getReceiverId())
                .receiverRole(request.getReceiverRole())
                .content(request.getContent())
                .build();

        messageRepository.save(msg);
        log.info("Message sent from {} ({}) to {}", senderName, senderRole, request.getReceiverId());

        notificationService.broadcast("NEW_MESSAGE",
                senderName + "|" + senderId + "|" + request.getReceiverId());

        return toResponse(msg);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(Long user1, Long user2) {
        return messageRepository.findConversation(user1, user2)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void markRead(Long userId, Long senderId) {
        messageRepository.markReadBySender(userId, senderId);
    }

    @Transactional(readOnly = true)
    public List<Long> getUnreadSenders(Long userId) {
        return messageRepository.findSendersWithUnread(userId);
    }

    private MessageResponse toResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .senderName(m.getSenderName())
                .senderRole(m.getSenderRole())
                .receiverId(m.getReceiverId())
                .receiverRole(m.getReceiverRole())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .isRead(m.isRead())
                .build();
    }
}
