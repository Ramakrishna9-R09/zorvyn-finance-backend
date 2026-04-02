package com.zorvyn.finance.dto;

import com.zorvyn.finance.enums.Category;
import com.zorvyn.finance.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancialRecordDTOs {

    @Data
    public static class FinancialRecordResponse {
        private Long id;
        private BigDecimal amount;
        private TransactionType type;
        private Category category;
        private LocalDate transactionDate;
        private String description;
        private String notes;
        private UserDTO createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class CreateFinancialRecordRequest {
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        @NotNull(message = "Type is required")
        private TransactionType type;

        @NotNull(message = "Category is required")
        private Category category;

        @NotNull(message = "Transaction date is required")
        private LocalDate transactionDate;

        private String description;
        private String notes;
    }

    @Data
    public static class UpdateFinancialRecordRequest {
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;
        private TransactionType type;
        private Category category;
        private LocalDate transactionDate;
        private String description;
        private String notes;
    }

    @Data
    public static class FinancialRecordFilter {
        private TransactionType type;
        private Category category;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
    }
}
