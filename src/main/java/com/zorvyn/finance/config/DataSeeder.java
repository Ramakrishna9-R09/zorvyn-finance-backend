package com.zorvyn.finance.config;

import com.zorvyn.finance.entity.FinancialRecord;
import com.zorvyn.finance.entity.Role;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.Category;
import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.enums.UserStatus;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.RoleRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            Role viewerRole = createRoleIfMissing(RoleName.VIEWER, "Can only view dashboard data");
            Role analystRole = createRoleIfMissing(RoleName.ANALYST, "Can view records and access insights");
            Role adminRole = createRoleIfMissing(RoleName.ADMIN, "Full management access");

            User adminUser = userRepository.findByEmail("admin@zorvyn.com")
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email("admin@zorvyn.com")
                            .password(passwordEncoder.encode("admin123"))
                            .firstName("Admin")
                            .lastName("User")
                            .status(UserStatus.ACTIVE)
                            .role(adminRole)
                            .build()));

            if (financialRecordRepository.count() == 0) {
                financialRecordRepository.saveAll(List.of(
                        createRecord(new BigDecimal("5000.00"), TransactionType.INCOME, Category.SALARY, LocalDate.of(2024, 3, 1), "Monthly Salary", "March 2024 salary payment", adminUser),
                        createRecord(new BigDecimal("1200.00"), TransactionType.EXPENSE, Category.RENT, LocalDate.of(2024, 3, 2), "Monthly Rent", "Apartment rent", adminUser),
                        createRecord(new BigDecimal("150.00"), TransactionType.EXPENSE, Category.GROCERIES, LocalDate.of(2024, 3, 3), "Grocery Shopping", "Weekly groceries", adminUser),
                        createRecord(new BigDecimal("2000.00"), TransactionType.INCOME, Category.FREELANCE, LocalDate.of(2024, 3, 5), "Freelance Project", "Web development project", adminUser),
                        createRecord(new BigDecimal("80.00"), TransactionType.EXPENSE, Category.UTILITIES, LocalDate.of(2024, 3, 6), "Electric Bill", "Monthly electricity", adminUser),
                        createRecord(new BigDecimal("300.00"), TransactionType.EXPENSE, Category.ENTERTAINMENT, LocalDate.of(2024, 3, 7), "Concert Tickets", "Weekend concert", adminUser)
                ));
            }
        };
    }

    private Role createRoleIfMissing(RoleName roleName, String description) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(roleName)
                        .description(description)
                        .build()));
    }

    private FinancialRecord createRecord(BigDecimal amount,
                                         TransactionType type,
                                         Category category,
                                         LocalDate date,
                                         String description,
                                         String notes,
                                         User createdBy) {
        return FinancialRecord.builder()
                .amount(amount)
                .type(type)
                .category(category)
                .transactionDate(date)
                .description(description)
                .notes(notes)
                .createdBy(createdBy)
                .build();
    }
}
