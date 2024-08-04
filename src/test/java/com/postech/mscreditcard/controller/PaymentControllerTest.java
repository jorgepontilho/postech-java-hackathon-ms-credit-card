package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.PaymentClientDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.*;
import com.postech.mscreditcard.gateway.PaymentGateway;
import com.postech.mscreditcard.usecase.PaymentUseCase;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootApplication
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentUseCase paymentUseCase;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentController paymentController;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CreatePayment {
        @Test
        public void test_payment_creation_with_valid_dto_return_http_200() {
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            when(paymentGateway.createPayment(paymentDTO)).thenReturn(paymentDTO);

            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(paymentDTO, response.getBody());
        }

        @Test
        public void test_payment_creation_with_general_exception_return_http_400() {
            // Arrange
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("12345678901");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            when(paymentGateway.createPayment(paymentDTO)).thenThrow(new RuntimeException("Exception"));

            // Act
            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Exception", response.getBody());
        }

        @Test
        public void test_payment_creation_with_unauthenticated_user_return_http_401() {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        public void test_payment_creation_with_no_limit_card_return_http_402() {
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            when(paymentGateway.createPayment(paymentDTO)).thenThrow(new NoLimitCardException("Cartão sem limite"));

            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
            assertEquals("Cartão sem limite", response.getBody());
        }

        @Test
        public void test_payment_creation_with_invalid_payment_exception_return_http_500() {
            PaymentDTO paymentDTO = new PaymentDTO();
            HttpServletRequest request = mock(HttpServletRequest.class);

            doThrow(new InvalidPaymentException("Problema nos dados de pagamento")).when(paymentUseCase).validatePayment(paymentDTO);

            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Problema nos dados de pagamento", response.getBody());
        }

        @Test
        public void test_payment_creation_with_non_existent_card_return_http_500() {
            // Arrange
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("12345678901");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            doThrow(new CardNotFoundException("Cartão não encontrado")).when(paymentUseCase).validatePayment(paymentDTO);

            // Act & Assert
            ResponseEntity<?> response = paymentController.createPayment(request, paymentDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Cartão não encontrado", response.getBody());
        }
    }

    @Nested
    class listAllPayments {
        @Test
        public void test_list_all_payments_return_http_200() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

            when(request.getAttribute("error")).thenReturn(null);
            List<PaymentDTO> payments = Arrays.asList(new PaymentDTO(), new PaymentDTO());
            when(paymentGateway.listAllPayments()).thenReturn(payments);

            ResponseEntity<?> response = paymentController.listAllPayments(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(payments, response.getBody());
        }

        @Test
        public void test_list_all_payments_with_general_exception_return_http_400() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

            when(request.getAttribute("error")).thenReturn(null);
            when(paymentGateway.listAllPayments()).thenThrow(new RuntimeException("Generic error"));

            ResponseEntity<?> response = paymentController.listAllPayments(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Generic error", response.getBody());
        }

        @Test
        public void test_list_all_payments_with_unauthenticated_user_return_http_401() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = paymentController.listAllPayments(request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        // Handles UnknownErrorException and returns HTTP 500 status code with the error message
        @Test
        public void test_list_all_payments_with_unknown_exception_return_http_500() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

            when(request.getAttribute("error")).thenReturn(null);
            when(paymentGateway.listAllPayments()).thenThrow(new UnknownErrorException("Unknown error", new Exception()));

            ResponseEntity<?> response = paymentController.listAllPayments(request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unknown error", response.getBody());
        }
    }

    @Nested
    class getPayment {
        @Test
        public void test_get_payment_return_http_200(){
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);
            HttpServletRequest request = mock(HttpServletRequest.class);
            String validKey = "valid-key";
            PaymentClientDTO paymentClientDTO = new PaymentClientDTO();

            when(paymentGateway.findByUuid(validKey)).thenReturn(paymentClientDTO);

            ResponseEntity<?> response = paymentController.getPayment(request, validKey);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void test_get_payment_with_general_exception_return_http_400() {
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(paymentGateway.findByUuid("generic-error-key")).thenThrow(new RuntimeException("Generic error"));

            ResponseEntity<?> response = paymentController.getPayment(request, "generic-error-key");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void test_get_payment_with_unauthenticated_user_return_http_401() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = paymentController.getPayment(request, "valid-key");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        public void test_get_payment_with_unknown_exception_return_http_500(){
            PaymentGateway paymentGateway = mock(PaymentGateway.class);
            PaymentUseCase paymentUseCase = mock(PaymentUseCase.class);
            PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(paymentGateway.findByUuid("not-found-key")).thenThrow(new NotFoundException("Pagamento nￃﾣo encontrado"));

            ResponseEntity<?> notFoundResponse = paymentController.getPayment(request, "not-found-key");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, notFoundResponse.getStatusCode());

            when(paymentGateway.findByUuid("unknown-error-key")).thenThrow(new UnknownErrorException("Unknown error", new Exception()));

            ResponseEntity<?> unknownErrorResponse = paymentController.getPayment(request, "unknown-error-key");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, unknownErrorResponse.getStatusCode());
        }

    }
}