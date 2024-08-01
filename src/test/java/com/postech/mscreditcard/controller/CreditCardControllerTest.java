package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.exceptions.GlobalExceptionHandler;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.usecase.CreditCardUseCase;
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
public class CreditCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditCardUseCase creditCardUseCase;

    @Mock
    private CreditCardGateway creditCardGateway;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        CreditCardController creditCardController = new CreditCardController(creditCardUseCase, creditCardGateway);

        mockMvc = MockMvcBuilders.standaloneSetup(creditCardController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CreateCreditCard {

        @Test
        void shouldCreateCreditCard() throws Exception {
            CardDTO cardDTO = NewEntitiesHelper.newCreditCard().toDTO();

            when(creditCardGateway.createCard(any(CardDTO.class))).thenReturn(cardDTO);

            mockMvc.perform(post("/api/cartao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(cardDTO)))
                    .andExpect(status().isOk());
        }
    }
}