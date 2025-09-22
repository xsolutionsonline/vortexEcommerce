package io.vortex.commerce.orderservice.infrastructure.out.persistence.entity;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import jakarta.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class RoleEntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería persistir y recuperar una RoleEntity correctamente")
    void shouldSaveAndRetrieveRole() {
        // Arrange
        RoleEntity role = new RoleEntity(null, Role.ROLE_USER);

        // Act
        RoleEntity savedRole = entityManager.persistAndFlush(role);
        entityManager.clear();
        RoleEntity foundRole = entityManager.find(RoleEntity.class, savedRole.getId());

        // Assert
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getId()).isNotNull();
        assertThat(foundRole.getName()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("Debería lanzar una excepción al intentar guardar un rol con nombre duplicado")
    void shouldThrowExceptionWhenSavingDuplicateRoleName() {
        // Arrange
        entityManager.persistAndFlush(new RoleEntity(null, Role.ROLE_ADMIN));
        RoleEntity duplicateRole = new RoleEntity(null, Role.ROLE_ADMIN);

        // Act & Assert
        assertThatThrownBy(() -> entityManager.persistAndFlush(duplicateRole))
                .isInstanceOf(PersistenceException.class);
    }
}