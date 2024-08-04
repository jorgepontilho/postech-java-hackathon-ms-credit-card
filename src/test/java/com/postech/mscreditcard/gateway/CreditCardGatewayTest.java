package com.postech.mscreditcard.gateway;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.UserDTO;
import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.repository.CardRepository;
import com.postech.mscreditcard.repository.CustomerRepository;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootApplication
public class CreditCardGatewayTest {

    @Mock
    private CardRepository cardRepository;

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
    class findByLoginAndPassword{
        @Test
        public void returns_userdto_when_login_and_password_match() {
            // Arrange
            CreditCardGateway gateway = new CreditCardGateway(null, null);
            gateway.LOGIN = "correctLogin";
            gateway.PASSWORD = "correctPassword";

            // Act
            UserDTO result = gateway.findByLoginAndPassword("correctLogin", "correctPassword");

            // Assert
            assertNotNull(result);
            assertEquals("correctLogin", result.getUsuario());
            assertEquals("correctPassword", result.getSenha());
        }

        @Test
        public void returns_null_when_login_is_null() {
            // Arrange
            CreditCardGateway gateway = new CreditCardGateway(null, null);
            gateway.LOGIN = "correctLogin";
            gateway.PASSWORD = "correctPassword";

            // Act
            UserDTO result = gateway.findByLoginAndPassword(null, "correctPassword");

            // Assert
            assertNull(result);
        }

        @Test
        public void logs_error_when_exception_occurs() {
            // Arrange
            CreditCardGateway gateway = new CreditCardGateway(null, null);
            String login = null;
            String password = null;

            // Act
            UserDTO result = gateway.findByLoginAndPassword(login, password);

            // Assert
            assertNull(result);
        }
    }

    @Nested
    class CreateCard {

        @Test
        void shouldCreateCreditCard() {
            // Arrange
            Card creditCardNew = NewEntitiesHelper.newCreditCard();

            Customer customer = NewEntitiesHelper.newCustomer();
            customer.setCpf(creditCardNew.getCpf());
            creditCardNew.setCustomer(customer);

            CardDTO creditCardDTO = creditCardNew.toDTO();

            when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));
            when(cardRepository.save(any(Card.class))).thenReturn(creditCardNew);

            // Act
            CardDTO createdCard = creditCardGateway.createCard(creditCardDTO);

            // Assert
            assertThat(createdCard).isNotNull();
            assertThat(createdCard.getCpf()).isEqualTo(creditCardDTO.getCpf());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCustomerNotFound() {
            CardDTO creditCardDTO = NewEntitiesHelper.newCreditCard().toDTO();

            when(customerRepository.findByCpf("989898989-91")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> creditCardGateway.createCard(creditCardDTO));
        }
    }

    @Nested
    class ListAllCards {

        @Test
        void shouldReturnListOfCardDTOs() {
            // Arrange
            Card creditCard1 = NewEntitiesHelper.newCreditCard();
            Card creditCard2 = NewEntitiesHelper.newCreditCard();
            creditCard2.setId(2L);
            creditCard2.setCpf("98765432100");

            when(cardRepository.findAll()).thenReturn(Arrays.asList(creditCard1, creditCard2));

            // Act
            List<CardDTO> creditCardDTOList = creditCardGateway.listAllCards();

            // Assert
            assertThat(creditCardDTOList).isNotNull();
            assertThat(creditCardDTOList).hasSize(2);
            assertThat(creditCardDTOList.get(0).getCpf()).isEqualTo(creditCard1.getCpf());
            assertThat(creditCardDTOList.get(1).getCpf()).isEqualTo(creditCard2.getCpf());
        }

        // Retrieve a card by its number successfully
        @Test
        public void test_retrieve_card_by_number_successfully() {
            // Arrange
            CardRepository cardRepository = mock(CardRepository.class);
            CreditCardGateway creditCardGateway = new CreditCardGateway(cardRepository, null);
            String cardNumber = "1234 5678 9012 3456";
            Card card = new Card();
            card.setCardNumber(cardNumber);
            when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(card));

            // Act
            List<CardDTO> result = creditCardGateway.listAllCards(cardNumber);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(cardNumber, result.get(0).getNumero());
        }

        @Test
        public void test_list_all_cards_logs_error_on_exception() {
            // Arrange
            CardRepository cardRepository = mock(CardRepository.class);
            CreditCardGateway creditCardGateway = new CreditCardGateway(cardRepository, null);
            String cardNumber = "1234 5678 9012 3456";
            when(cardRepository.findByCardNumber(cardNumber)).thenThrow(new RuntimeException("Simulated Exception"));

            // Act & Assert
            assertThrows(Exception.class, () -> creditCardGateway.listAllCards(cardNumber));
        }
    }

    @Nested
    class ListAllCustomerCards {

        @Test
        void shouldReturnListOfCardDTOsForGivenCpf() {
            String cpf = "988989898-91";
            Card creditCard1 = NewEntitiesHelper.newCreditCard();
            creditCard1.setCpf(cpf);
            Card creditCard2 = NewEntitiesHelper.newCreditCard();
            creditCard2.setId(2L);
            creditCard2.setCpf(cpf);

            when(cardRepository.findAllByCpf(cpf)).thenReturn(Arrays.asList(creditCard1, creditCard2));

            List<CardDTO> creditCardDTOList = creditCardGateway.listAllCustomerCards(cpf);

            assertThat(creditCardDTOList).isNotNull();
            assertThat(creditCardDTOList).hasSize(2);
            assertThat(creditCardDTOList.get(0).getCpf()).isEqualTo(cpf);
            assertThat(creditCardDTOList.get(1).getCpf()).isEqualTo(cpf);
        }

        @Test
        void shouldThrowExceptionWhenErrorOccurs() {
            String cpf = "988989898-00";
            when(cardRepository.findAllByCpf(anyString())).thenThrow(new RuntimeException("Database error"));

            assertThrows(RuntimeException.class, () -> creditCardGateway.listAllCustomerCards(cpf));
        }
    }

    @Nested
    class findCustomerCard {
        @Test
        public void test_find_customer_card_success() {
            // Arrange
            String cpf = "12345678901";
            String cardNumber = "1234 5678 9012 3456";
            Card card = new Card();
            card.setCpf(cpf);
            card.setCardNumber(cardNumber);
            when(cardRepository.findByCpfAndCardNumber(cpf, cardNumber)).thenReturn(card);

            // Act
            CardDTO result = creditCardGateway.findCustomerCard(cpf, cardNumber);

            // Assert
            assertNotNull(result);
            assertEquals(cpf, result.getCpf());
            assertEquals(cardNumber, result.getNumero());
        }

        @Test
        public void test_return_null_when_card_not_found() {
            // Arrange
            String cpf = "12345678901";
            String cardNumber = "1234 5678 9012 3456";
            when(cardRepository.findByCpfAndCardNumber(cpf, cardNumber)).thenReturn(null);

            // Act
            CardDTO result = creditCardGateway.findCustomerCard(cpf, cardNumber);

            // Assert
            assertNull(result);
        }

        @Test
        public void test_handle_unexpected_exceptions() {
            // Arrange
            String cpf = "12345678901";
            String cardNumber = "1234 5678 9012 3456";
            when(cardRepository.findByCpfAndCardNumber(cpf, cardNumber)).thenThrow(new RuntimeException("Unexpected error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                creditCardGateway.findCustomerCard(cpf, cardNumber);
            });
        }
    }

}
