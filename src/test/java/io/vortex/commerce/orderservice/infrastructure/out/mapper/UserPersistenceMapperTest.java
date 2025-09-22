package io.vortex.commerce.orderservice.infrastructure.out.mapper;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.domain.model.User;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapperImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {UserPersistenceMapperImpl.class})
class UserPersistenceMapperTest {

    @Autowired
    private UserPersistenceMapper userPersistenceMapper;

    @Test
    @DisplayName("Debería mapear UserEntity a User de dominio correctamente")
    void shouldMapUserEntityToDomain() {
        // Arrange
        RoleEntity adminRoleEntity = new RoleEntity(1, Role.ROLE_ADMIN);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setRoles(Set.of(adminRoleEntity));

        // Act
        User user = userPersistenceMapper.toDomain(userEntity);

        // Assert
        assertEquals("testuser", user.username());
        assertEquals(1, user.roles().size());
        assertTrue(user.roles().contains(Role.ROLE_ADMIN));
    }

    @Test
    @DisplayName("Debería mapear User de dominio a UserEntity correctamente")
    void shouldMapDomainUserToEntity() {
        // Arrange
        User user = new User(1L, "testuser", "password", Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

        // Act
        UserEntity userEntity = userPersistenceMapper.toEntity(user);

        // Assert
        assertEquals("testuser", userEntity.getUsername());
        assertEquals(2, userEntity.getRoles().size());
        Set<Role> roleNames = userEntity.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
        assertTrue(roleNames.containsAll(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN)));
    }
}