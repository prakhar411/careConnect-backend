package com.careconnect.service;

import com.careconnect.dto.request.ComplianceRequest;
import com.careconnect.dto.response.ComplianceResponse;
import com.careconnect.entity.ComplianceRecord;
import com.careconnect.entity.Organization;
import com.careconnect.enums.ComplianceStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.ComplianceRecordRepository;
import com.careconnect.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceService {

    private final ComplianceRecordRepository complianceRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public ComplianceResponse create(Long orgUserId, ComplianceRequest request) {
        Organization org = organizationRepository.findByUserId(orgUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgUserId));

        ComplianceRecord record = ComplianceRecord.builder()
                .organization(org)
                .nurseName(request.getNurseName())
                .requirement(request.getRequirement())
                .dueDate(request.getDueDate())
                .status(request.getStatus() != null ? request.getStatus() : ComplianceStatus.PENDING)
                .notes(request.getNotes())
                .build();

        complianceRepository.save(record);
        return toResponse(record);
    }

    public List<ComplianceResponse> getByOrganization(Long orgUserId) {
        Organization org = organizationRepository.findByUserId(orgUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgUserId));
        return complianceRepository.findByOrganizationId(org.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ComplianceResponse updateStatus(Long id, ComplianceStatus status) {
        ComplianceRecord record = complianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ComplianceRecord", id));
        record.setStatus(status);
        complianceRepository.save(record);
        return toResponse(record);
    }

    public void delete(Long id) {
        complianceRepository.deleteById(id);
    }

    private ComplianceResponse toResponse(ComplianceRecord r) {
        return ComplianceResponse.builder()
                .id(r.getId())
                .organizationId(r.getOrganization() != null ? r.getOrganization().getId() : null)
                .nurseName(r.getNurseName())
                .requirement(r.getRequirement())
                .dueDate(r.getDueDate())
                .status(r.getStatus())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
