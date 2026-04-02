package com.zorvyn.finance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.enums.UserStatus;
import com.zorvyn.finance.repository.RoleRepository;
import com.zorvyn.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FinanceBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void ensureViewerUserExists() {
        if (!userRepository.existsByEmail("viewer@zorvyn.com")) {
            User viewer = User.builder()
                    .email("viewer@zorvyn.com")
                    .password(passwordEncoder.encode("viewer123"))
                    .firstName("View")
                    .lastName("Only")
                    .status(UserStatus.ACTIVE)
                    .role(roleRepository.findByName(RoleName.VIEWER).orElseThrow())
                    .build();
            userRepository.save(viewer);
        }
    }

    @Test
    void loginReturnsJwtForSeedAdmin() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@zorvyn.com",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertThat(json.path("data").path("user").path("role").asText()).isEqualTo("ADMIN");
    }

    @Test
    void viewerCannotCreateFinancialRecord() throws Exception {
        String token = loginAndGetToken("viewer@zorvyn.com", "viewer123");

        mockMvc.perform(post("/api/financial-records")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 2500,
                                  "type": "INCOME",
                                  "category": "FREELANCE",
                                  "transactionDate": "2026-04-02",
                                  "description": "Forbidden test"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void dashboardIsAvailableForAdmin() throws Exception {
        String token = loginAndGetToken("admin@zorvyn.com", "admin123");

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.summary.totalIncome").exists())
                .andExpect(jsonPath("$.data.recentActivity").isArray());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).path("data").path("token").asText();
    }
}
