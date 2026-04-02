package com.zorvyn.finance.dto;

import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private RoleName role;
    private LocalDateTime createdAt;
}
