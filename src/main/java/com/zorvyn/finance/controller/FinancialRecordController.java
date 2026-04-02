package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.FinancialRecordDTOs;
import com.zorvyn.finance.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial-records")
@Tag(name = "Financial Records", description = "Financial record management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FinancialRecordController {
    private final FinancialRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Get all records", description = "Retrieve all financial records with optional filters")
    public ResponseEntity<ApiResponse<List<FinancialRecordDTOs.FinancialRecordResponse>>> getAllRecords(
            @ModelAttribute FinancialRecordDTOs.FinancialRecordFilter filter) {
        List<FinancialRecordDTOs.FinancialRecordResponse> records = recordService.getAllRecords(filter);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Get record by ID", description = "Retrieve a specific financial record")
    public ResponseEntity<ApiResponse<FinancialRecordDTOs.FinancialRecordResponse>> getRecordById(@PathVariable Long id) {
        FinancialRecordDTOs.FinancialRecordResponse record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    @Operation(summary = "Create record", description = "Create a new financial record (Analyst/Admin only)")
    public ResponseEntity<ApiResponse<FinancialRecordDTOs.FinancialRecordResponse>> createRecord(
            @Valid @RequestBody FinancialRecordDTOs.CreateFinancialRecordRequest request) {
        FinancialRecordDTOs.FinancialRecordResponse record = recordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Record created successfully", record));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update record", description = "Update a financial record (Admin only)")
    public ResponseEntity<ApiResponse<FinancialRecordDTOs.FinancialRecordResponse>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordDTOs.UpdateFinancialRecordRequest request) {
        FinancialRecordDTOs.FinancialRecordResponse record = recordService.updateRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success("Record updated successfully", record));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete record", description = "Delete a financial record (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Record deleted successfully", null));
    }
}
