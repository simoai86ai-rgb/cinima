package com.cinemaone.platform.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CinemaUserDetailsService implements UserDetailsService {
    private final AdminUserRepository repository;

    public CinemaUserDetailsService(AdminUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser admin = repository.findByEmailIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return User.withUsername(admin.getEmail())
            .password(admin.getPasswordHash())
            .roles(admin.getRole().name())
            .disabled(!admin.isEnabled())
            .build();
    }
}
