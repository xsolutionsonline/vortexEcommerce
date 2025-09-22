package io.vortex.commerce.orderservice.infrastructure.out.persistence.entity;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import jakarta.persistence.PersistenceException;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserEntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería persistir y recuperar un UserEntity con sus roles correctamente")
    void shouldSaveAndRetrieveUserWithRoles() {
        // Arrange
        RoleEntity roleUser = entityManager.persist(new RoleEntity(null, Role.ROLE_USER));
        RoleEntity roleAdmin = entityManager.persist(new RoleEntity(null, Role.ROLE_ADMIN));

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRoles(Set.of(roleUser, roleAdmin));

        // Act
        UserEntity savedUser = entityManager.persistAndFlush(user);
        entityManager.clear();
        UserEntity foundUser = entityManager.find(UserEntity.class, savedUser.getId());

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getRoles()).hasSize(2);

        Set<Role> foundRoles = foundUser.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
        assertThat(foundRoles).containsExactlyInAnyOrder(Role.ROLE_USER, Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Debería lanzar una excepción al intentar guardar un usuario con nombre duplicado")
    void shouldThrowExceptionWhenSavingDuplicateUsername() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setUsername("duplicate_user");
        user1.setPassword("pass1");
        entityManager.persistAndFlush(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername("duplicate_user");
        user2.setPassword("pass2");

        // Act & Assert
        assertThatThrownBy(() -> entityManager.persistAndFlush(user2))
                .isInstanceOf(PersistenceException.class);
    }
}