package io.vortex.commerce.orderservice.infrastructure.config;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaRoleRepository;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin-username}")
    private String adminUsername;

    @Value("${app.security.admin-password}")
    private String adminPassword;

    @Value("${app.security.test-user-username}")
    private String testUserUsername;

    @Value("${app.security.test-user-password}")
    private String testUserPassword;


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        Map<Role, RoleEntity> roles = Arrays.stream(Role.values())
                .collect(Collectors.toMap(
                        roleEnum -> roleEnum,
                        roleEnum -> roleRepository.findByName(roleEnum)
                                .orElseGet(() -> {
                                    log.info("Creating role: {}", roleEnum.name());
                                    return roleRepository.save(new RoleEntity(null, roleEnum));
                                })
                ));

        createUserIfNotFound(adminUsername, adminPassword, Set.of(roles.get(Role.ROLE_ADMIN), roles.get(Role.ROLE_USER)));

        createUserIfNotFound(testUserUsername, testUserPassword, Set.of(roles.get(Role.ROLE_USER)));

        log.info("Data initialization finished.");
    }

    private void createUserIfNotFound(String username, String password, Set<RoleEntity> roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            log.info("Creating user: {}", username);
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            userRepository.save(user);
            log.info("User '{}' created successfully.", username);
        } else {
            log.info("User '{}' already exists. Skipping creation.", username);
        }
    }
}