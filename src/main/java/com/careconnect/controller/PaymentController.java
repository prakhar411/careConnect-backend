package com.careconnect.controller;

import com.careconnect.dto.request.MonthlySalaryRequest;
import com.careconnect.dto.request.PaymentRequest;
import com.careconnect.dto.request.ProcessPaymentRequest;
import com.careconnect.dto.request.ShiftPaymentRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.PaymentResponse;
import com.careconnect.enums.PaymentStatus;
import com.careconnect.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    // ── Existing ──────────────────────────────────────────────────────────────

    @PostMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Create a generic payment for a nurse (legacy)")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created", paymentService.createPayment(nurseUserId, request)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get full payment history for a nurse")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByNurse(nurseUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}/total")
    @Operation(summary = "Get total processed earnings for a nurse")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalEarnings(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalEarnings(nurseUserId)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updateStatus(
            @PathVariable Long id, @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", paymentService.updateStatus(id, status)));
    }

    // ── Shift Payments (Patient → Nurse) ─────────────────────────────────────

    @PostMapping("/shift/{nurseUserId}")
    @Operation(summary = "Nurse marks a shift as complete — creates PENDING payment for patient")
    public ResponseEntity<ApiResponse<PaymentResponse>> markShiftComplete(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody ShiftPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shift marked complete", paymentService.markShiftComplete(nurseUserId, request)));
    }

    @GetMapping("/patient/{patientUserId}/pending")
    @Operation(summary = "Get all pending shift payments for a patient")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingByPatient(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPendingShiftsByPatient(patientUserId)));
    }

    @PostMapping("/patient/{patientUserId}/pay")
    @Operation(summary = "Patient pays pending shifts for an appointment (simulation)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> processPatientPayment(
            @PathVariable Long patientUserId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully",
                paymentService.processPatientPayment(patientUserId, request)));
    }

    // ── Monthly Salary (Org → Nurse) ──────────────────────────────────────────

    @PostMapping("/org/{orgUserId}/salary")
    @Operation(summary = "Org processes monthly salary for a nurse (simulation)")
    public ResponseEntity<ApiResponse<PaymentResponse>> processMonthlySalary(
            @PathVariable Long orgUserId,
            @Valid @RequestBody MonthlySalaryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary processed successfully",
                        paymentService.processMonthlySalary(orgUserId, request)));
    }

    @GetMapping("/org/{orgUserId}/history")
    @Operation(summary = "Get all salary payments made by an organization")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getOrgSalaryHistory(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getOrgSalaryHistory(orgUserId)));
    }
}
