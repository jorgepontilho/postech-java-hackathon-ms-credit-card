package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.CardNotFoundException;
import com.postech.mscreditcard.exceptions.InvalidPaymentException;
import com.postech.mscreditcard.exceptions.NoLimitCardException;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.gateway.PaymentGateway;
import com.postech.mscreditcard.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private CreditCardGateway creditCardGateway;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class ValidatePayment {

        @Test
        void test_payment_validated_successfully() {
            // Arrange
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setValor(new BigDecimal("100.00"));

            CardDTO cardDTO = new CardDTO();
            cardDTO.setCpf("123.456.789-00");
            cardDTO.setNumero("1234 5678 1234 5678");
            cardDTO.setDataValidade("12/25");
            cardDTO.setLimite(new BigDecimal("200.00"));

            CreditCardGateway creditCardGateway = mock(CreditCardGateway.class);
            when(creditCardGateway.findCustomerCard(anyString(), anyString())).thenReturn(cardDTO);
            when(creditCardGateway.listAllCustomerCards(anyString())).thenReturn(List.of(cardDTO));

            PaymentUseCase paymentUseCase = new PaymentUseCase(null, null, creditCardGateway);

            // Act & Assert
            assertDoesNotThrow(() -> paymentUseCase.validatePayment(paymentDTO));
        }

        @Test
        void shouldThrowCardNotFoundExceptionWhenCardNotFound() {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setCvv("123");
            paymentDTO.setValor(new BigDecimal("100.00"));

            when(creditCardGateway.findCustomerCard(anyString(), anyString())).thenReturn(null);

            assertThrows(CardNotFoundException.class, () -> paymentUseCase.validatePayment(paymentDTO));
        }

        @Test
        void shouldThrowInvalidPaymentExceptionWhenCardIsExpired() {
            // Arrange
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("01/20"); // Expired date
            paymentDTO.setValor(new BigDecimal("100.00"));

            CardDTO cardDTO = new CardDTO();
            cardDTO.setCpf("123.456.789-00");
            cardDTO.setNumero("1234 5678 1234 5678");
            cardDTO.setDataValidade("01/20"); // Expired date
            cardDTO.setLimite(new BigDecimal("200.00"));

            CreditCardGateway creditCardGateway = mock(CreditCardGateway.class);
            when(creditCardGateway.findCustomerCard(anyString(), anyString())).thenReturn(cardDTO);

            PaymentUseCase paymentUseCase = new PaymentUseCase(null, null, creditCardGateway);

            // Act & Assert
            assertThrows(InvalidPaymentException.class, () -> paymentUseCase.validatePayment(paymentDTO));
        }

        @Test
        void shouldThrowNoLimitCardExceptionWhenCardHasNoLimit() {
            // Arrange
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCpf("123.456.789-00");
            paymentDTO.setNumero("1234 5678 1234 5678");
            paymentDTO.setDataValidade("12/25");
            paymentDTO.setValor(new BigDecimal("300.00")); // Exceeds limit

            CardDTO cardDTO = new CardDTO();
            cardDTO.setCpf("123.456.789-00");
            cardDTO.setNumero("1234 5678 1234 5678");
            cardDTO.setDataValidade("12/25");
            cardDTO.setLimite(new BigDecimal("200.00")); // Insufficient limit

            when(creditCardGateway.findCustomerCard(anyString(), anyString())).thenReturn(cardDTO);
            when(creditCardGateway.listAllCustomerCards(anyString())).thenReturn(Collections.singletonList(cardDTO));

            PaymentUseCase paymentUseCase = new PaymentUseCase(null, null, creditCardGateway);

            // Act & Assert
            assertThrows(NoLimitCardException.class, () -> paymentUseCase.validatePayment(paymentDTO));
        }
    }

    @Nested
    class IsValidExpirationDate {

        @Test
        void shouldReturnTrueForValidExpirationDate() {
            assertTrue(paymentUseCase.isValidExpirationDate("12/99"));
        }

        @Test
        void shouldReturnFalseForInvalidExpirationDate() {
            assertFalse(paymentUseCase.isValidExpirationDate("01/20"));
        }

        @Test
        void shouldReturnFalseForMalformedExpirationDate() {
            assertFalse(paymentUseCase.isValidExpirationDate("invalid-date"));
        }
    }
}