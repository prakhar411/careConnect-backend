package com.careconnect.entity;

import com.careconnect.enums.CredentialStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credentials")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", nullable = false)
    private NurseProfile nurse;

    @Column(nullable = false)
    private String credentialType;

    private String issuedBy;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CredentialStatus status = CredentialStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
