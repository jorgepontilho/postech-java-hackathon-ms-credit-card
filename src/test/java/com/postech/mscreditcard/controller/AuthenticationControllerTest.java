package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.UserDTO;
import com.postech.mscreditcard.exceptions.GlobalExceptionHandler;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.security.TokenService;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootApplication
public class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditCardGateway creditCardGateway;

    @Mock
    private TokenService tokenService;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        AuthenticationController authenticationController = new AuthenticationController(creditCardGateway);
        authenticationController.setTokenService(tokenService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class Autenticacao {

        @Test
        void shouldAuthenticate() throws Exception {
            UserDTO userDto = new UserDTO();
            userDto.setUsuario("test");
            userDto.setSenha("123");
            when(creditCardGateway.findByLoginAndPassword(any(),any())).thenReturn(userDto);
            mockMvc.perform(post("/api/autenticacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(userDto)))
                    .andExpect(status().isOk());
        }
    }
}