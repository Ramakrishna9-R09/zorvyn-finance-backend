package com.zorvyn.finance.dto;

import com.zorvyn.finance.enums.RoleName;
import com.zorvyn.finance.enums.UserStatus;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private UserStatus status;
    private RoleName role;
}
