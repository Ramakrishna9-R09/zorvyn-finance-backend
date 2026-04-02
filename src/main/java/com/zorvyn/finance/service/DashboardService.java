package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.DashboardDTOs;
import com.zorvyn.finance.entity.FinancialRecord;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialRecordService financialRecordService;

    public DashboardDTOs.DashboardResponse getDashboardData() {
        List<FinancialRecord> records = financialRecordRepository.findAll((root, query, cb) -> cb.isNull(root.get("deletedAt")));

        DashboardDTOs.DashboardResponse response = new DashboardDTOs.DashboardResponse();
        response.setSummary(buildSummary(records));
        response.setCategoryBreakdown(buildCategoryBreakdown(records));
        response.setTrends(buildMonthlyTrends(records));
        response.setRecentActivity(financialRecordService.getRecentActivity());
        return response;
    }

    private DashboardDTOs.DashboardSummary buildSummary(List<FinancialRecord> records) {
        BigDecimal totalIncome = sumByType(records, TransactionType.INCOME);
        BigDecimal totalExpenses = sumByType(records, TransactionType.EXPENSE);
        BigDecimal totalAmount = records.stream()
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardDTOs.DashboardSummary summary = new DashboardDTOs.DashboardSummary();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetBalance(totalIncome.subtract(totalExpenses));
        summary.setTotalTransactions((long) records.size());
        summary.setAverageTransactionAmount(records.isEmpty()
                ? BigDecimal.ZERO
                : totalAmount.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP));
        return summary;
    }

    private List<DashboardDTOs.CategorySummary> buildCategoryBreakdown(List<FinancialRecord> records) {
        BigDecimal totalExpenses = sumByType(records, TransactionType.EXPENSE);

        return records.stream()
                .filter(record -> record.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(record -> record.getCategory().name()))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(FinancialRecord::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    DashboardDTOs.CategorySummary summary = new DashboardDTOs.CategorySummary();
                    summary.setCategory(entry.getKey());
                    summary.setTotal(total);
                    summary.setCount((long) entry.getValue().size());
                    summary.setPercentage(totalExpenses.compareTo(BigDecimal.ZERO) == 0
                            ? 0.0
                            : total.multiply(BigDecimal.valueOf(100))
                                    .divide(totalExpenses, 2, RoundingMode.HALF_UP)
                                    .doubleValue());
                    return summary;
                })
                .sorted(Comparator.comparing(DashboardDTOs.CategorySummary::getTotal).reversed())
                .toList();
    }

    private List<DashboardDTOs.TrendData> buildMonthlyTrends(List<FinancialRecord> records) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        return records.stream()
                .collect(Collectors.groupingBy(record -> YearMonth.from(record.getTransactionDate())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    BigDecimal income = sumByType(entry.getValue(), TransactionType.INCOME);
                    BigDecimal expenses = sumByType(entry.getValue(), TransactionType.EXPENSE);

                    DashboardDTOs.TrendData trendData = new DashboardDTOs.TrendData();
                    trendData.setPeriod(entry.getKey().format(formatter));
                    trendData.setIncome(income);
                    trendData.setExpenses(expenses);
                    trendData.setNet(income.subtract(expenses));
                    return trendData;
                })
                .toList();
    }

    private BigDecimal sumByType(List<FinancialRecord> records, TransactionType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
