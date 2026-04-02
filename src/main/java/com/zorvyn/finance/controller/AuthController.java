package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.AuthDTOs;
import com.zorvyn.finance.dto.UserDTO;
import com.zorvyn.finance.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user and return JWT token")
    public ResponseEntity<ApiResponse<AuthDTOs.LoginResponse>> login(@Valid @RequestBody AuthDTOs.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.authenticate(request)));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new viewer account")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody AuthDTOs.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authService.register(request)));
    }
}
