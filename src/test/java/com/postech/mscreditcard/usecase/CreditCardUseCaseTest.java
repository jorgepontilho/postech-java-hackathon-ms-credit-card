package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CreditCardDTO;
import com.postech.mscreditcard.exceptions.MaxCardsException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootApplication
public class CreditCardUseCaseTest {

    @Mock
    private CreditCardGateway creditCardGateway;

    @InjectMocks
    private CreditCardUseCase creditCardUseCase;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class ValidateCardCreation {

        @Test
        void shouldThrowExceptionWhenCreditCardDTOIsNull() {
            assertThrows(IllegalArgumentException.class, () -> creditCardUseCase.validateCardCreation(null));
        }

        @Test
        void shouldNotThrowExceptionWhenCustomerHasLessThanMaxCards() {
            CreditCardDTO creditCardDTO = new CreditCardDTO();
            creditCardDTO.setCpf("12345678900");

            when(creditCardGateway.listAllCustomerCards(anyString())).thenReturn(Collections.emptyList());

            creditCardUseCase.validateCardCreation(creditCardDTO);
        }

        @Test
        void shouldThrowMaxCardsExceptionWhenCustomerHasMaxCards() {
            CreditCardDTO creditCardDTO = new CreditCardDTO();
            creditCardDTO.setCpf("12345678900");

            when(creditCardGateway.listAllCustomerCards(anyString())).thenReturn(Arrays.asList(new CreditCardDTO(), new CreditCardDTO()));

            assertThrows(MaxCardsException.class, () -> creditCardUseCase.validateCardCreation(creditCardDTO));
        }

        @Test
        void shouldThrowUnknownErrorExceptionWhenUnknownErrorOccurs() {
            CreditCardDTO creditCardDTO = new CreditCardDTO();
            creditCardDTO.setCpf("989898989-91");

            when(creditCardGateway.listAllCustomerCards(anyString())).thenThrow(new UnknownErrorException());

            assertThrows(UnknownErrorException.class, () -> creditCardUseCase.validateCardCreation(creditCardDTO));
        }
    }
}
