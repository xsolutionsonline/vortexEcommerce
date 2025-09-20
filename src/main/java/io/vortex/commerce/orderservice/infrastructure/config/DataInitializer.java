package io.vortex.commerce.orderservice.infrastructure.config;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaRoleRepository;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:password123}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing roles...");
        Arrays.stream(Role.values()).forEach(roleEnum ->
                roleRepository.findByName(roleEnum)
                        .orElseGet(() -> {
                            log.info("Creating role: {}", roleEnum.name());
                            return roleRepository.save(new RoleEntity(null, roleEnum));
                        })
        );

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            log.info("Creating admin user with username: {}", adminUsername);

            RoleEntity adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("FATAL: ROLE_ADMIN not found after initialization."));

            UserEntity adminUser = new UserEntity();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);
            log.info("Admin user '{}' created successfully.", adminUsername);
        } else {
            log.info("Admin user '{}' already exists. Skipping creation.", adminUsername);
        }
    }
}