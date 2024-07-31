package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.CreditCardDTO;
import com.postech.mscreditcard.entity.CreditCard;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.repository.CreditCardRepository;
import com.postech.mscreditcard.repository.CustomerRepository;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootApplication
public class CreditCardGatewayTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreditCardGateway creditCardGateway;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CreateCard {

        @Test
        void shouldCreateCreditCard() {
            // Arrange
            CreditCard creditCardNew = NewEntitiesHelper.newCreditCard();

            Customer customer = NewEntitiesHelper.newCustomer();
            customer.setCpf(creditCardNew.getCpf());
            creditCardNew.setCustomer(customer);

            CreditCardDTO creditCardDTO = creditCardNew.toDTO();

            when(customerRepository.findByCpf(creditCardDTO.getCpf())).thenReturn(Optional.of(customer));
            when(creditCardRepository.save(creditCardNew)).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            CreditCardDTO createdCard = creditCardGateway.createCard(creditCardDTO);

            // Assert
            assertThat(createdCard).isNotNull();
            assertThat(createdCard.getCpf()).isEqualTo(creditCardDTO.getCpf());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCustomerNotFound() {
            CreditCardDTO creditCardDTO = NewEntitiesHelper.newCreditCard().toDTO();

            when(customerRepository.findByCpf("989898989-91")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> creditCardGateway.createCard(creditCardDTO));
        }
    }

    @Nested
    class ListAllCards {

        @Test
        void shouldReturnListOfCreditCardDTOs() {
            // Arrange
            CreditCard creditCard1 = NewEntitiesHelper.newCreditCard();
            CreditCard creditCard2 = NewEntitiesHelper.newCreditCard();
            creditCard2.setId(2);
            creditCard2.setCpf("98765432100");

            when(creditCardRepository.findAll()).thenReturn(Arrays.asList(creditCard1, creditCard2));

            // Act
            List<CreditCardDTO> creditCardDTOList = creditCardGateway.listAllCards();

            // Assert
            assertThat(creditCardDTOList).isNotNull();
            assertThat(creditCardDTOList).hasSize(2);
            assertThat(creditCardDTOList.get(0).getCpf()).isEqualTo(creditCard1.getCpf());
            assertThat(creditCardDTOList.get(1).getCpf()).isEqualTo(creditCard2.getCpf());
        }
    }

    @Nested
    class ListAllCustomerCards {

        @Test
        void shouldReturnListOfCreditCardDTOsForGivenCpf() {
            String cpf = "988989898-91";
            CreditCard creditCard1 = NewEntitiesHelper.newCreditCard();
            creditCard1.setCpf(cpf);
            CreditCard creditCard2 = NewEntitiesHelper.newCreditCard();
            creditCard2.setId(2);
            creditCard2.setCpf(cpf);

            when(creditCardRepository.findAllByCpf(cpf)).thenReturn(Arrays.asList(creditCard1, creditCard2));

            List<CreditCardDTO> creditCardDTOList = creditCardGateway.listAllCustomerCards(cpf);

            assertThat(creditCardDTOList).isNotNull();
            assertThat(creditCardDTOList).hasSize(2);
            assertThat(creditCardDTOList.get(0).getCpf()).isEqualTo(cpf);
            assertThat(creditCardDTOList.get(1).getCpf()).isEqualTo(cpf);
        }

        @Test
        void shouldThrowExceptionWhenErrorOccurs() {
            String cpf = "988989898-00";
            when(creditCardRepository.findAllByCpf(anyString())).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> creditCardGateway.listAllCustomerCards(cpf));
        }
    }
}
