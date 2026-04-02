package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.FinancialRecordDTOs;
import com.zorvyn.finance.dto.UserDTO;
import com.zorvyn.finance.entity.FinancialRecord;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.exception.BadRequestException;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialRecordService {
    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FinancialRecordDTOs.FinancialRecordResponse> getAllRecords(FinancialRecordDTOs.FinancialRecordFilter filter) {
        return financialRecordRepository.findAll(buildSpecification(filter)).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinancialRecordDTOs.FinancialRecordResponse getRecordById(Long id) {
        return mapToResponse(getActiveRecord(id));
    }

    public FinancialRecordDTOs.FinancialRecordResponse createRecord(FinancialRecordDTOs.CreateFinancialRecordRequest request) {
        User currentUser = getCurrentUser();
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();

        return mapToResponse(financialRecordRepository.save(record));
    }

    public FinancialRecordDTOs.FinancialRecordResponse updateRecord(Long id, FinancialRecordDTOs.UpdateFinancialRecordRequest request) {
        FinancialRecord record = getActiveRecord(id);

        if (request.getAmount() != null) {
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be positive");
            }
            record.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            record.setType(request.getType());
        }
        if (request.getCategory() != null) {
            record.setCategory(request.getCategory());
        }
        if (request.getTransactionDate() != null) {
            record.setTransactionDate(request.getTransactionDate());
        }
        if (request.getDescription() != null) {
            record.setDescription(request.getDescription());
        }
        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        return mapToResponse(financialRecordRepository.save(record));
    }

    public void deleteRecord(Long id) {
        FinancialRecord record = getActiveRecord(id);
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<FinancialRecordDTOs.FinancialRecordResponse> getRecentActivity() {
        return financialRecordRepository.findTop5ByDeletedAtIsNullOrderByTransactionDateDescCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private FinancialRecord getActiveRecord(Long id) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        if (record.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Financial record not found with id: " + id);
        }
        return record;
    }

    private Specification<FinancialRecord> buildSpecification(FinancialRecordDTOs.FinancialRecordFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (filter != null) {
                if (filter.getType() != null) {
                    predicates.add(cb.equal(root.get("type"), filter.getType()));
                }
                if (filter.getCategory() != null) {
                    predicates.add(cb.equal(root.get("category"), filter.getCategory()));
                }
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.getStartDate()));
                }
                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.getEndDate()));
                }
                if (filter.getMinAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
                }
                if (filter.getMaxAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("amount"), filter.getMaxAmount()));
                }
            }

            query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private FinancialRecordDTOs.FinancialRecordResponse mapToResponse(FinancialRecord record) {
        FinancialRecordDTOs.FinancialRecordResponse response = new FinancialRecordDTOs.FinancialRecordResponse();
        response.setId(record.getId());
        response.setAmount(record.getAmount());
        response.setType(record.getType());
        response.setCategory(record.getCategory());
        response.setTransactionDate(record.getTransactionDate());
        response.setDescription(record.getDescription());
        response.setNotes(record.getNotes());
        response.setCreatedAt(record.getCreatedAt());
        response.setCreatedBy(mapUser(record.getCreatedBy()));
        return response;
    }

    private UserDTO mapUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole().getName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
