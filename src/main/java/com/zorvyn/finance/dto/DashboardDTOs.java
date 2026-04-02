package com.zorvyn.finance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDTOs {

    @Data
    public static class DashboardSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netBalance;
        private Long totalTransactions;
        private BigDecimal averageTransactionAmount;
    }

    @Data
    public static class CategorySummary {
        private String category;
        private BigDecimal total;
        private Double percentage;
        private Long count;
    }

    @Data
    public static class TrendData {
        private String period;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal net;
    }

    @Data
    public static class DashboardResponse {
        private DashboardSummary summary;
        private List<CategorySummary> categoryBreakdown;
        private List<TrendData> trends;
        private List<FinancialRecordDTOs.FinancialRecordResponse> recentActivity;
    }
}
