package com.careconnect.service;

import com.careconnect.entity.Notification;
import com.careconnect.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // ── SSE broadcast (kept for real-time push) ───────────────────────────────

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(()    -> emitters.remove(emitter));
        emitter.onError(e       -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        log.info("SSE client connected. Total: {}", emitters.size());
        return emitter;
    }

    public void broadcast(String eventName, String data) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }

    // ── DB-backed notifications ───────────────────────────────────────────────

    @Transactional
    public Notification pushToUser(Long userId, String type, String title, String message,
                                   Long entityId, String entityType) {
        Notification n = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(entityId)
                .relatedEntityType(entityType)
                .build();
        notificationRepository.save(n);
        broadcast(type, title + "|" + message);
        log.info("Notification pushed to user {}: {}", userId, title);
        return n;
    }

    @Transactional(readOnly = true)
    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }
}
