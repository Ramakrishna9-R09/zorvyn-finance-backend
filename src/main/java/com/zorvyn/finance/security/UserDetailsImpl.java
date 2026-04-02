package com.zorvyn.finance.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zorvyn.finance.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String role;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String email,
                           String firstName,
                           String lastName,
                           String password,
                           String role,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        String authority = "ROLE_" + user.getRole().getName().name();
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getRole().getName().name(),
                List.of(new SimpleGrantedAuthority(authority))
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
