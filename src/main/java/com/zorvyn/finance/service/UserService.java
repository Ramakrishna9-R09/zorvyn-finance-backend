package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.CreateUserRequest;
import com.zorvyn.finance.dto.UpdateUserRequest;
import com.zorvyn.finance.dto.UserDTO;
import com.zorvyn.finance.entity.Role;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.exception.BadRequestException;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.repository.RoleRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return mapToDTO(getUserEntity(id));
    }

    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        Role role = getRole(request.getRole());
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(role)
                .build();

        return mapToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = getUserEntity(id);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getRole() != null) {
            user.setRole(getRole(request.getRole()));
        }

        return mapToDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = getUserEntity(id);
        if (user.getRole().getName() == RoleName.ADMIN && userRepository.count() == 1) {
            throw new BadRequestException("Cannot delete the last admin user");
        }
        userRepository.delete(user);
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
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
