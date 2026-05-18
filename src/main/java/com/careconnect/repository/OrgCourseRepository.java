package com.careconnect.repository;

import com.careconnect.entity.OrgCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrgCourseRepository extends JpaRepository<OrgCourse, Long> {
    List<OrgCourse> findByOrgUserId(Long orgUserId);
    List<OrgCourse> findAllByOrderByCreatedAtDesc();
}
