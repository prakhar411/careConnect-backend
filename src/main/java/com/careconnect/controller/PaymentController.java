package com.careconnect.controller;

import com.careconnect.dto.request.PaymentRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/nurse/{nurseUserId}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Create a payment for a nurse")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created", paymentService.createPayment(nurseUserId, request)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get payment history for a nurse")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByNurse(nurseUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}/total")
    @Operation(summary = "Get total earnings for a nurse")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalEarnings(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalEarnings(nurseUserId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updateStatus(
            @PathVariable Long id, @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", paymentService.updateStatus(id, status)));
    }
}
