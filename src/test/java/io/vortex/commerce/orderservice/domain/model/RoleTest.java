package io.vortex.commerce.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleTest {

    @Test
    @DisplayName("Enum should contain all expected role values")
    void enumShouldContainAllExpectedValues() {
        assertNotNull(Role.valueOf("ROLE_USER"));
        assertNotNull(Role.valueOf("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Enum should have the correct number of values")
    void enumShouldHaveCorrectNumberOfValues() {
        assertEquals(2, Role.values().length);
    }

    @Test
    @DisplayName("Enum values should have correct string representation")
    void enumValuesShouldHaveCorrectStringRepresentation() {
        assertEquals("ROLE_USER", Role.ROLE_USER.name());
        assertEquals("ROLE_ADMIN", Role.ROLE_ADMIN.name());
    }
}