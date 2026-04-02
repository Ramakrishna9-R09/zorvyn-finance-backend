package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.DashboardDTOs;
import com.zorvyn.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard summary and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Get dashboard data", description = "Retrieve complete dashboard summary including totals, trends, and recent activity")
    public ResponseEntity<ApiResponse<DashboardDTOs.DashboardResponse>> getDashboard() {
        DashboardDTOs.DashboardResponse dashboard = dashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
