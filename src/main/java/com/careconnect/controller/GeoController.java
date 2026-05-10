package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/states")
    public ResponseEntity<ApiResponse<List<String>>> getStates() {
        return ResponseEntity.ok(ApiResponse.success(geoService.getStates()));
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> getCities(@RequestParam String state) {
        return ResponseEntity.ok(ApiResponse.success(geoService.getCities(state)));
    }
}
