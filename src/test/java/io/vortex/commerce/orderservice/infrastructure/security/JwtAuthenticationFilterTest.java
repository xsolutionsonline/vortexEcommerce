package io.vortex.commerce.orderservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto de seguridad después de cada prueba
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debería autenticar al usuario y refrescar el token cuando el JWT es válido")
    void doFilterInternal_shouldAuthenticateUser_whenTokenIsValid() throws ServletException, IOException {
        // Arrange
        String jwt = "valid.jwt.token";
        String newJwt = "new.refreshed.token";
        String username = "testuser";
        String authHeader = "Bearer " + jwt;
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenProvider.getUsernameFromToken(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenProvider.validateToken(jwt, userDetails)).thenReturn(true);
        when(tokenProvider.generateToken(userDetails)).thenReturn(newJwt);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "La autenticación no debería ser nula");
        assertEquals(username, ((UserDetails) authentication.getPrincipal()).getUsername());

        verify(response).addHeader("Authorization", "Bearer " + newJwt);
        verify(response).addHeader("Access-Control-Expose-Headers", "Authorization");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("No debería autenticar si el token es inválido")
    void doFilterInternal_shouldNotAuthenticate_whenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        String jwt = "invalid.jwt.token";
        String username = "testuser";
        String authHeader = "Bearer " + jwt;
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenProvider.getUsernameFromToken(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenProvider.validateToken(jwt, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "La autenticación debería ser nula");
        verify(response, never()).addHeader(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Debería continuar la cadena de filtros si no hay cabecera de autorización")
    void doFilterInternal_shouldContinueChain_whenNoAuthHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Debería continuar la cadena si la cabecera no empieza con 'Bearer '")
    void doFilterInternal_shouldContinueChain_whenHeaderNotStartsWithBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic somecredentials");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenProvider, userDetailsService);
    }
}