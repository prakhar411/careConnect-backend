package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "handoff_notes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HandoffNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderOrgUserId;

    @Column(nullable = false)
    private Long recipientNurseUserId;

    private Long jobId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime sentAt;
}
