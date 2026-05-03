package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "management_team")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ManagementTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    private String email;
    private String phone;
    private LocalDate joinDate;

    @Builder.Default
    private String status = "Active";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
