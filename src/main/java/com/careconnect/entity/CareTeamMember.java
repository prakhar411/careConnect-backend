package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_team_members")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CareTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientUserId;

    private Long appointmentId;

    private Long teamMemberId;      // references management_team.id

    @Column(nullable = false)
    private String memberName;      // denormalized for easy fetch

    @Column(nullable = false)
    private String memberRole;      // "Cardiologist", "Neurologist", etc.

    private Long addedByNurseUserId;
    private String addedByNurseName;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
