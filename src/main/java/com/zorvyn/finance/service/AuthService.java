package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.AuthDTOs;
import com.zorvyn.finance.dto.UserDTO;
import com.zorvyn.finance.entity.Role;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.enums.UserStatus;
import com.zorvyn.finance.exception.BadRequestException;
import com.zorvyn.finance.repository.RoleRepository;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.security.JwtUtils;
import com.zorvyn.finance.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthDTOs.LoginResponse authenticate(AuthDTOs.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        UserDTO userDTO = mapToDTO(user);

        AuthDTOs.LoginResponse response = new AuthDTOs.LoginResponse();
        response.setToken(jwt);
        response.setUser(userDTO);
        return response;
    }

    public UserDTO register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        Role viewerRole = roleRepository.findByName(RoleName.VIEWER)
                .orElseThrow(() -> new BadRequestException("Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(UserStatus.ACTIVE)
                .role(viewerRole)
                .build();

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
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
