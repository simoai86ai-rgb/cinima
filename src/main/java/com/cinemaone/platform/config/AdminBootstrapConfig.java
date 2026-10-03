package com.cinemaone.platform.config;

import com.cinemaone.platform.security.AdminRole;
import com.cinemaone.platform.security.AdminUser;
import com.cinemaone.platform.security.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    @Bean
    ApplicationRunner bootstrapAdmin(
        AdminUserRepository repository,
        PasswordEncoder passwordEncoder,
        @Value("${app.bootstrap-admin.enabled:false}") boolean enabled,
        @Value("${app.bootstrap-admin.email:}") String email,
        @Value("${app.bootstrap-admin.password:}") String password,
        @Value("${app.bootstrap-admin.display-name:Cinema One Admin}") String displayName,
        @Value("${app.bootstrap-admin.reset-existing-password:false}") boolean resetExistingPassword
    ) {
        return args -> {
            if (!enabled) return;
            if (email == null || email.isBlank() || password == null || password.length() < 12) {
                throw new IllegalStateException("Bootstrap admin is enabled but APP_ADMIN_EMAIL/APP_ADMIN_PASSWORD is missing or password is shorter than 12 characters.");
            }

            String normalizedEmail = email.trim().toLowerCase();
            AdminUser admin = repository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
            if (admin == null) {
                admin = new AdminUser();
                admin.setEmail(normalizedEmail);
                admin.setPasswordHash(passwordEncoder.encode(password));
                admin.setDisplayName(displayName);
                admin.setRole(AdminRole.ADMIN);
                admin.setEnabled(true);
                repository.save(admin);
                log.info("Development bootstrap admin created for {}. Disable bootstrap in production.", normalizedEmail);
                return;
            }

            boolean changed = false;
            if (resetExistingPassword && !passwordEncoder.matches(password, admin.getPasswordHash())) {
                admin.setPasswordHash(passwordEncoder.encode(password));
                changed = true;
            }
            if (!admin.isEnabled()) {
                admin.setEnabled(true);
                changed = true;
            }
            if (admin.getRole() != AdminRole.ADMIN) {
                admin.setRole(AdminRole.ADMIN);
                changed = true;
            }
            if (displayName != null && !displayName.isBlank() && !displayName.equals(admin.getDisplayName())) {
                admin.setDisplayName(displayName);
                changed = true;
            }
            if (changed) {
                repository.save(admin);
                log.info("Development bootstrap admin synchronized for {}.", normalizedEmail);
            }
        };
    }
}
