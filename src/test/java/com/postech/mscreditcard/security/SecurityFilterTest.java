package com.postech.mscreditcard.security;

import com.postech.mscreditcard.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootApplication
public class SecurityFilterTest {
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Nested
    class validToken {
        @Test
        void testValidToken_withValidToken() {
            // Arrange
            String token = "validToken";
            String login = "userLogin";
            when(tokenService.validateToken(token)).thenReturn(login);

            // Act
            User result = securityFilter.validToken(token);

            // Assert
            assertNotNull(result);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void testValidToken_withInvalidToken() {
            // Arrange
            String token = "invalidToken";
            when(tokenService.validateToken(token)).thenReturn("");

            // Act
            User result = securityFilter.validToken(token);

            // Assert
            assertNull(result);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void testValidToken_withException() {
            // Arrange
            String token = "exceptionToken";
            when(tokenService.validateToken(token)).thenThrow(new RuntimeException());

            // Act
            User result = securityFilter.validToken(token);

            // Assert
            assertNull(result);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

    }

    @Nested
    class validateRequest {
        @Test
        public void test_token_validated_and_corresponds_to_valid_user() {
            // Arrange
            SecurityFilter securityFilter = new SecurityFilter();
            TokenService tokenService = mock(TokenService.class);
            securityFilter.tokenService = tokenService;
            when(tokenService.validateToken("validToken")).thenReturn("userLogin");

            // Act
            User user = securityFilter.validToken("validToken");

            // Assert
            assertNotNull(user);
        }
    }

    @Nested
    class recoverToken {
        @Test
        public void test_token_missing_from_authorization_header() {
            // Arrange
            SecurityFilter securityFilter = new SecurityFilter();
            HttpServletRequest request = mock(HttpServletRequest.class);

            // Act
            String token = securityFilter.recoverToken(request);

            // Assert
            assertNull(token);
        }
    }
}
