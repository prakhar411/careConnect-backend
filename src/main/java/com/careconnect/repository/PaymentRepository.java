package com.careconnect.repository;

import com.careconnect.entity.Payment;
import com.careconnect.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByNurseIdOrderByPaymentDateDesc(Long nurseId);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.nurse.id = :nurseId AND p.status = 'PROCESSED'")
    BigDecimal sumProcessedByNurseId(@Param("nurseId") Long nurseId);
}
