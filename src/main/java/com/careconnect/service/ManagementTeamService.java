package com.careconnect.service;

import com.careconnect.dto.request.TeamMemberRequest;
import com.careconnect.dto.response.TeamMemberResponse;
import com.careconnect.entity.ManagementTeamMember;
import com.careconnect.entity.Organization;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.ManagementTeamRepository;
import com.careconnect.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementTeamService {

    private final ManagementTeamRepository teamRepository;
    private final OrganizationRepository organizationRepository;

    public List<TeamMemberResponse> getByOrg(Long orgUserId) {
        Organization org = organizationRepository.findByUserId(orgUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgUserId));
        return teamRepository.findByOrganizationId(org.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public TeamMemberResponse add(Long orgUserId, TeamMemberRequest request) {
        Organization org = organizationRepository.findByUserId(orgUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgUserId));

        ManagementTeamMember member = ManagementTeamMember.builder()
                .organization(org)
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .joinDate(request.getJoinDate())
                .build();

        return toResponse(teamRepository.save(member));
    }

    @Transactional
    public TeamMemberResponse toggleStatus(Long id) {
        ManagementTeamMember member = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember", id));
        member.setStatus(member.getStatus().equals("Active") ? "Inactive" : "Active");
        return toResponse(teamRepository.save(member));
    }

    @Transactional
    public void delete(Long id) {
        if (!teamRepository.existsById(id)) throw new ResourceNotFoundException("TeamMember", id);
        teamRepository.deleteById(id);
    }

    private TeamMemberResponse toResponse(ManagementTeamMember m) {
        return TeamMemberResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .role(m.getRole())
                .email(m.getEmail())
                .phone(m.getPhone())
                .joinDate(m.getJoinDate())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
