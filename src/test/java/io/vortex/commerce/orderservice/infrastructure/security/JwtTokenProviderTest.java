package io.vortex.commerce.orderservice.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String secret = "d63c94533438eaa66d19a6c256ba8d50f653dc80c1987373f7f5ad72e4b39516";
    private final int expirationMs = 300000; // 5 minutes

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", expirationMs);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("Debería generar un token y extraer el nombre de usuario correctamente")
    void shouldGenerateAndExtractUsernameFromToken() {
        // Arrange
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        // Act
        String token = jwtTokenProvider.generateToken(userDetails);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("testuser", extractedUsername);
    }

    @Test
    @DisplayName("Debería validar un token correctamente")
    void shouldValidateTokenSuccessfully() {
        // Arrange
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("No debería validar un token si el nombre de usuario no coincide")
    void shouldFailValidationForMismatchedUsername() {
        // Arrange
        UserDetails userDetails1 = new User("user1", "password", Collections.emptyList());
        UserDetails userDetails2 = new User("user2", "password", Collections.emptyList());
        String token = jwtTokenProvider.generateToken(userDetails1);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, userDetails2);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debería lanzar ExpiredJwtException para un token expirado")
    void shouldThrowExpiredJwtExceptionForExpiredToken() throws InterruptedException {
        // Arrange
       JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(shortLivedProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(shortLivedProvider, "jwtExpirationInMs", 1); // 1 ms
        shortLivedProvider.init();

        UserDetails userDetails = new User("expiredUser", "password", Collections.emptyList());
        String token = shortLivedProvider.generateToken(userDetails);

        Thread.sleep(50);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            shortLivedProvider.validateToken(token, userDetails);
        });
    }

    @Test
    @DisplayName("Debería lanzar MalformedJwtException para un token con formato incorrecto")
    void shouldThrowMalformedJwtExceptionForInvalidToken() {
        // Arrange
        String malformedToken = "esto.no.es.un.token";

        // Act & Assert
        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.getUsernameFromToken(malformedToken);
        });
    }
}