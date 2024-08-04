package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.exceptions.*;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.usecase.CreditCardUseCase;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootApplication
public class CreditCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditCardUseCase creditCardUseCase;

    @Mock
    private CreditCardGateway creditCardGateway;

    @InjectMocks
    private CreditCardController creditCardController;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);

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
    class CreateCard {

        @Test
        void test_create_card_return_http_200() throws Exception {
            CardDTO cardDTO = NewEntitiesHelper.newCreditCard().toDTO();

            when(creditCardGateway.createCard(any(CardDTO.class))).thenReturn(cardDTO);

            mockMvc.perform(post("/api/cartao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(cardDTO)))
                    .andExpect(status().isOk());
        }

        @Test
        void test_create_card_with_unauthenticated_user_return_http_401() throws Exception {
            CardDTO cardDTO = new CardDTO();

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        void test_create_card_max_cards_reached_return_http_403() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CardDTO cardDTO = new CardDTO(1L, "12345678901", new BigDecimal("1000.00"), "1234 5678 1234 5678", "12/25", "123");
            doThrow(new MaxCardsException("Máximo de cartões atingido")).when(creditCardUseCase).validateCardCreation(any(CardDTO.class));

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Máximo de cartões atingido", response.getBody());
        }

        @Test
        void test_create_card_with_generic_error_return_http_404() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CardDTO cardDTO = new CardDTO(1L, "12345678901", new BigDecimal("1000.00"), "1234 5678 1234 5678", "12/23", "123");
            doThrow(new RuntimeException("Generic error")).when(creditCardGateway).createCard(any(CardDTO.class));

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Generic error", response.getBody());
        }

        @Test
        void test_create_card_with_existing_card_return_http_500() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CardDTO cardDTO = new CardDTO(1L, "12345678901", new BigDecimal("1000.00"), "1234 5678 1234 5678", "12/23", "123");
            doThrow(new CardExistException("Número de Cartão já existe.")).when(creditCardUseCase).validateCardCreation(any(CardDTO.class));

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Número de Cartão já existe.", response.getBody());
        }

        @Test
        void test_create_card_with_unknown_error_return_http_500() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CardDTO cardDTO = new CardDTO(1L, "12345678901", new BigDecimal("1000.00"), "1234 5678 1234 5678", "12/23", "123");
            doThrow(new UnknownErrorException("Unknown error", new UnknownErrorException())).when(creditCardGateway).createCard(any(CardDTO.class));

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unknown error", response.getBody());
        }

        @Test
        void test_create_card_for_non_existent_customer_return_http_500() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CardDTO cardDTO = new CardDTO(1L, "12345678901", new BigDecimal("1000.00"), "1234 5678 1234 5678", "12/23", "123");
            doThrow(new NotFoundException("Cliente não encontrado")).when(creditCardGateway).createCard(any(CardDTO.class));

            ResponseEntity<?> response = creditCardController.createCard(request, cardDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Cliente não encontrado", response.getBody());
        }
    }

    @Nested
    class ListAllCards {
        @Test
        void test_list_all_cards_return_http_200() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);

            CreditCardGateway creditCardGateway = mock(CreditCardGateway.class);
            List<CardDTO> cards = Arrays.asList(new CardDTO(), new CardDTO());
            when(creditCardGateway.listAllCards()).thenReturn(cards);

            CreditCardController controller = new CreditCardController(creditCardUseCase, creditCardGateway);
            ResponseEntity<?> response = controller.listAllCards(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(cards, response.getBody());
        }

        @Test
        void test_list_all_with_unauthenticated_user_return_http_401() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = creditCardController.listAllCards(request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

    }
}