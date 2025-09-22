package io.vortex.commerce.orderservice.infrastructure.security;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Debería cargar UserDetails cuando el usuario existe")
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Arrange
        String username = "testuser";
        RoleEntity roleEntity = new RoleEntity(1, Role.ROLE_USER);
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(Set.of(roleEntity));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Debería lanzar UsernameNotFoundException cuando el usuario no existe")
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found with username: " + username, exception.getMessage());
    }
}