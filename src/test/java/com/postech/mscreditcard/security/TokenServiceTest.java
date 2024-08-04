package com.postech.mscreditcard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.postech.mscreditcard.dto.UserDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootApplication
public class TokenServiceTest {

    @Nested
    class generateToken{

        @Test
        public void generates_valid_jwt_token() {
            // Arrange
            TokenService tokenService = new TokenService();
            UserDTO userDTO = new UserDTO("testUser", "testPassword");
            ReflectionTestUtils.setField(tokenService, "secret", "testSecret");

            // Act
            String token = tokenService.generateToken(userDTO);

            // Assert
            assertNotNull(token);
        }

        @Test
        public void handles_null_userdto_input() {
            // Arrange
            TokenService tokenService = new TokenService();
            ReflectionTestUtils.setField(tokenService, "secret", "testSecret");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                tokenService.generateToken(null);
            });
        }

        @Test
        public void handles_jwtcreationexception_and_throws_runtimeexception() {
            // Arrange
            TokenService tokenService = Mockito.spy(new TokenService());
            UserDTO userDTO = new UserDTO("testUser", "testPassword");
            ReflectionTestUtils.setField(tokenService, "secret", "testSecret");

            Mockito.doThrow(JWTCreationException.class).when(tokenService).generateToken(Mockito.any(UserDTO.class));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                tokenService.generateToken(userDTO);
            });
        }
    }

    @Nested
    class validateToken{

        @Test
        public void validates_valid_token() {
            // Arrange
            TokenService tokenService = new TokenService();
            UserDTO userDTO = new UserDTO("testUser", "testPassword");
            ReflectionTestUtils.setField(tokenService, "secret", "testSecret");
            String token = tokenService.generateToken(userDTO);

            // Act
            String result = tokenService.validateToken(token);

            // Assert
            assertEquals("testUser", result);
        }

        @Test
        public void returns_empty_string_for_invalid_token() {
            // Arrange
            TokenService tokenService = new TokenService();
            ReflectionTestUtils.setField(tokenService, "secret", "testSecret");
            String invalidToken = "invalidToken";

            // Act
            String result = tokenService.validateToken(invalidToken);

            // Assert
            assertEquals("", result);
        }
    }
}
