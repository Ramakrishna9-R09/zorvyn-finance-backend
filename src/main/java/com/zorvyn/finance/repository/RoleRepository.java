package com.zorvyn.finance.repository;

import com.zorvyn.finance.entity.Role;
import com.zorvyn.finance.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
