package com.careconnect.repository;

import com.careconnect.entity.Payment;
import com.careconnect.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByNurseIdOrderByPaymentDateDesc(Long nurseId);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.nurse.id = :nurseId AND p.status = 'PROCESSED'")
    BigDecimal sumProcessedByNurseId(@Param("nurseId") Long nurseId);

    // All PENDING shift payments for a patient (to show what they owe)
    @Query("SELECT p FROM Payment p WHERE p.paidByUserId = :patientUserId AND p.paymentStructure = 'SHIFT' AND p.status = 'PENDING' ORDER BY p.paymentDate DESC")
    List<Payment> findPendingShiftsByPatientUserId(@Param("patientUserId") Long patientUserId);

    // PENDING shifts for a specific appointment (patient pays these)
    @Query("SELECT p FROM Payment p WHERE p.appointment.id = :appointmentId AND p.paidByUserId = :patientUserId AND p.status = 'PENDING'")
    List<Payment> findPendingShiftsByAppointment(@Param("appointmentId") Long appointmentId, @Param("patientUserId") Long patientUserId);

    // Org salary payment history
    @Query("SELECT p FROM Payment p WHERE p.paidByUserId = :orgUserId AND p.paymentStructure = 'MONTHLY_SALARY' ORDER BY p.paymentDate DESC")
    List<Payment> findSalaryPaymentsByOrgUserId(@Param("orgUserId") Long orgUserId);

    // All shift payments for nurse (patient-side) grouped view
    @Query("SELECT p FROM Payment p WHERE p.nurse.id = :nurseId AND p.paymentStructure = 'SHIFT' ORDER BY p.paymentDate DESC")
    List<Payment> findShiftPaymentsByNurseId(@Param("nurseId") Long nurseId);

    // All salary payments for nurse (org-side)
    @Query("SELECT p FROM Payment p WHERE p.nurse.id = :nurseId AND p.paymentStructure = 'MONTHLY_SALARY' ORDER BY p.paymentDate DESC")
    List<Payment> findSalaryPaymentsByNurseId(@Param("nurseId") Long nurseId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.nurse.id = :nurseId AND p.paymentStructure = 'SHIFT' AND p.status = 'PENDING'")
    BigDecimal sumPendingShiftsByNurseId(@Param("nurseId") Long nurseId);

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    // ALL shift payments for a patient (pending + processed) — for payment history
    @Query("SELECT p FROM Payment p WHERE p.paidByUserId = :patientUserId AND p.paymentStructure = 'SHIFT' ORDER BY p.paymentDate DESC")
    List<Payment> findAllShiftsByPatientUserId(@Param("patientUserId") Long patientUserId);
}
