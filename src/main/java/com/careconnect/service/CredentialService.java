package com.careconnect.service;

import com.careconnect.dto.request.CredentialRequest;
import com.careconnect.dto.response.CredentialResponse;
import com.careconnect.entity.Credential;
import com.careconnect.entity.NurseProfile;
import com.careconnect.enums.CredentialStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.CredentialRepository;
import com.careconnect.repository.NurseProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final NurseProfileRepository nurseProfileRepository;

    @Transactional
    public CredentialResponse addCredential(Long nurseUserId, CredentialRequest request) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));

        Credential credential = Credential.builder()
                .nurse(nurse)
                .credentialType(request.getCredentialType())
                .issuedBy(request.getIssuedBy())
                .issuedDate(request.getIssuedDate())
                .expiryDate(request.getExpiryDate())
                .documentUrl(request.getDocumentUrl())
                .build();

        credentialRepository.save(credential);
        return toResponse(credential);
    }

    public List<CredentialResponse> getByNurse(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return credentialRepository.findByNurseId(nurse.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CredentialResponse> getAll() {
        return credentialRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> getByOrg(Long orgUserId) {
        return credentialRepository.findByOrgUserId(orgUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CredentialResponse> getExpiringSoon(int daysAhead) {
        return credentialRepository.findExpiringSoon(LocalDate.now().plusDays(daysAhead))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CredentialResponse verify(Long id) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential", id));
        credential.setStatus(CredentialStatus.VERIFIED);
        credentialRepository.save(credential);
        return toResponse(credential);
    }

    private CredentialResponse toResponse(Credential c) {
        return CredentialResponse.builder()
                .id(c.getId())
                .nurseId(c.getNurse().getId())
                .nurseName(c.getNurse().getFullName())
                .credentialType(c.getCredentialType())
                .issuedBy(c.getIssuedBy())
                .issuedDate(c.getIssuedDate())
                .expiryDate(c.getExpiryDate())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
