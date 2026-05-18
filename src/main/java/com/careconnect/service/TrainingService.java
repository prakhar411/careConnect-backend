package com.careconnect.service;

import com.careconnect.dto.request.CompleteTrainingRequest;
import com.careconnect.dto.request.OrgCourseRequest;
import com.careconnect.entity.OrgCourse;
import com.careconnect.entity.TrainingRecord;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.OrgCourseRepository;
import com.careconnect.repository.TrainingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final OrgCourseRepository orgCourseRepository;
    private final TrainingRecordRepository trainingRecordRepository;

    // ── Org: add a mandatory course ──────────────────────────────────────────

    @Transactional
    public OrgCourse addOrgCourse(Long orgUserId, OrgCourseRequest req) {
        OrgCourse course = OrgCourse.builder()
                .orgUserId(orgUserId)
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .creditPoints(req.getCreditPoints())
                .mandatory(Boolean.TRUE.equals(req.getMandatory()))
                .build();
        return orgCourseRepository.save(course);
    }

    @Transactional
    public void deleteOrgCourse(Long id, Long orgUserId) {
        OrgCourse course = orgCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrgCourse", id));
        if (!course.getOrgUserId().equals(orgUserId))
            throw new BadRequestException("You can only delete your own courses.");
        orgCourseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public List<OrgCourse> getOrgCourses(Long orgUserId) {
        return orgCourseRepository.findByOrgUserId(orgUserId);
    }

    @Transactional(readOnly = true)
    public List<OrgCourse> getAllOrgCourses() {
        return orgCourseRepository.findAllByOrderByCreatedAtDesc();
    }

    // ── Nurse: complete a course ─────────────────────────────────────────────

    @Transactional
    public TrainingRecord completeTraining(Long nurseUserId, CompleteTrainingRequest req) {
        if (trainingRecordRepository.existsByNurseUserIdAndCourseName(nurseUserId, req.getCourseName())) {
            throw new BadRequestException("You have already completed this course.");
        }
        TrainingRecord record = TrainingRecord.builder()
                .nurseUserId(nurseUserId)
                .courseName(req.getCourseName())
                .category(req.getCategory())
                .creditPoints(req.getCreditPoints() != null ? req.getCreditPoints() : 0)
                .source(req.getSource() != null ? req.getSource() : "STATIC")
                .orgCourseId(req.getOrgCourseId())
                .build();
        return trainingRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<TrainingRecord> getNurseCompletions(Long nurseUserId) {
        return trainingRecordRepository.findByNurseUserIdOrderByCompletedAtDesc(nurseUserId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getNurseSummary(Long nurseUserId) {
        List<TrainingRecord> records = trainingRecordRepository.findByNurseUserIdOrderByCompletedAtDesc(nurseUserId);
        int totalCredits   = records.stream().mapToInt(r -> r.getCreditPoints() != null ? r.getCreditPoints() : 0).sum();
        long totalCourses  = records.size();
        return Map.of("totalCredits", totalCredits, "totalCourses", totalCourses, "completions", records);
    }
}
