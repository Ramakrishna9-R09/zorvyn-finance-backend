package com.zorvyn.finance.repository;

import com.zorvyn.finance.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {
    List<FinancialRecord> findTop5ByDeletedAtIsNullOrderByTransactionDateDescCreatedAtDesc();
}
