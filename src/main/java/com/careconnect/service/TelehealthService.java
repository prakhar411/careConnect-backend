package com.careconnect.service;

import com.careconnect.entity.TelehealthMedia;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.TelehealthMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelehealthService {

    private final TelehealthMediaRepository mediaRepo;
    private final FileStorageService        fileStorage;

    @Transactional
    public Map<String, Object> upload(MultipartFile file,
                                      Long nurseUserId, String nurseName,
                                      Long patientUserId, String patientName,
                                      String title, String category, String description) {
        String storedName = fileStorage.store(file);
        String original   = file.getOriginalFilename() != null ? file.getOriginalFilename() : storedName;
        String fileType   = detectFileType(original);

        TelehealthMedia media = TelehealthMedia.builder()
                .nurseUserId(nurseUserId)
                .nurseName(nurseName)
                .patientUserId(patientUserId)
                .patientName(patientName)
                .title(title)
                .category(category)
                .fileName(storedName)
                .originalFileName(original)
                .fileType(fileType)
                .description(description)
                .build();

        return toMap(mediaRepo.save(media));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getByNurse(Long nurseUserId) {
        return mediaRepo.findByNurseUserIdOrderByCreatedAtDesc(nurseUserId)
                .stream().map(this::toMap).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getByPatient(Long patientUserId) {
        return mediaRepo.findByPatientUserIdOrderByCreatedAtDesc(patientUserId)
                .stream().map(this::toMap).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        TelehealthMedia media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TelehealthMedia", id));
        try { fileStorage.delete(media.getFileName()); } catch (Exception ignored) {}
        mediaRepo.deleteById(id);
    }

    private Map<String, Object> toMap(TelehealthMedia m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",               m.getId());
        map.put("nurseUserId",      m.getNurseUserId());
        map.put("nurseName",        m.getNurseName());
        map.put("patientUserId",    m.getPatientUserId());
        map.put("patientName",      m.getPatientName());
        map.put("title",            m.getTitle());
        map.put("category",         m.getCategory());
        map.put("fileName",         m.getFileName());
        map.put("originalFileName", m.getOriginalFileName());
        map.put("fileType",         m.getFileType());
        map.put("description",      m.getDescription());
        map.put("createdAt",        m.getCreatedAt());
        return map;
    }

    private String detectFileType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mov")
                || lower.endsWith(".avi") || lower.endsWith(".mkv")) return "video";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".webp")) return "image";
        return "document";
    }
}
