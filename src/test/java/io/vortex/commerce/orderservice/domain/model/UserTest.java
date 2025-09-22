package io.vortex.commerce.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Record should be created with correct values and accessors should work")
    void recordCreationAndAccessors() {
        // Arrange
        Long id = 1L;
        String username = "testuser";
        String password = "password123";
        Set<Role> roles = Set.of(Role.ROLE_USER);

        // Act
        User user = new User(id, username, password, roles);

        // Assert
        assertEquals(id, user.id());
        assertEquals(username, user.username());
        assertEquals(password, user.password());
        assertEquals(roles, user.roles());
    }

    @Test
    @DisplayName("Equals and HashCode should be consistent for equal records")
    void equalsAndHashCode_shouldBeConsistent() {
        // Arrange
        User user1 = new User(1L, "testuser", "password123", Set.of(Role.ROLE_USER));
        User user2 = new User(1L, "testuser", "password123", Set.of(Role.ROLE_USER));
        User user3 = new User(2L, "anotheruser", "password456", Set.of(Role.ROLE_ADMIN));

        // Assert
        assertEquals(user1, user2, "Records with the same values should be equal.");
        assertNotEquals(user1, user3, "Records with different values should not be equal.");
        assertEquals(user1.hashCode(), user2.hashCode(), "Hash codes for equal records should be the same.");
    }

    @Test
    @DisplayName("toString should return a non-empty string containing field values")
    void toString_shouldReturnNonEmptyString() {
        // Arrange
        User user = new User(1L, "testuser", "password123", Set.of(Role.ROLE_USER));

        // Act
        String userString = user.toString();

        // Assert
        assertTrue(userString.contains("id=1"));
        assertTrue(userString.contains("username=testuser"));
        assertTrue(userString.contains("roles=[ROLE_USER]"));
    }
}