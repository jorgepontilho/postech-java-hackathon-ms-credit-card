package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.PaymentClientDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.repository.CardRepository;
import com.postech.mscreditcard.repository.CustomerRepository;
import com.postech.mscreditcard.repository.PaymentRepository;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PaymentGatewayTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private PaymentGateway paymentGateway;

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
    class CreatePayment {

        @Test
        void shouldCreatePayment() {
            Payment payment = NewEntitiesHelper.newPayment();
            PaymentDTO paymentDTO = payment.toDTO();

            when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(NewEntitiesHelper.newCreditCard()));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentDTO createdPayment = paymentGateway.createPayment(paymentDTO);

            assertThat(createdPayment).isNotNull();
            assertThat(createdPayment.getId()).isEqualTo(paymentDTO.getId());
        }
    }

    @Nested
    class createPaymentNotOK {
        // PaymentDTO status is updated correctly
        @Test
        public void test_paymentdto_status_updated_correctly() {
            // Arrange
            PaymentRepository paymentRepository = mock(PaymentRepository.class);
            CardRepository cardRepository = mock(CardRepository.class);
            PaymentGateway paymentGateway = new PaymentGateway(paymentRepository, cardRepository);
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setNumero("1234 5678 9012 3456");
            String status = "rejected";
            Card card = new Card();
            when(cardRepository.findByCardNumber(paymentDTO.getNumero())).thenReturn(Optional.of(card));

            // Act
            paymentGateway.createPaymentNotOK(paymentDTO, status);

            // Assert
            assertEquals(status, paymentDTO.getStatus());
        }

        // Card number does not exist in the repository
        @Test
        public void test_card_number_does_not_exist_in_repository() {
            // Arrange
            PaymentRepository paymentRepository = mock(PaymentRepository.class);
            CardRepository cardRepository = mock(CardRepository.class);
            PaymentGateway paymentGateway = new PaymentGateway(paymentRepository, cardRepository);
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setNumero("1234 5678 9012 3456");
            String status = "rejected";
            when(cardRepository.findByCardNumber(paymentDTO.getNumero())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                paymentGateway.createPaymentNotOK(paymentDTO, status);
            });
        }
    }

    @Nested
    class ListAllPayments {

        @Test
        void shouldReturnListOfPaymentDTOs() {
            Payment payment1 = NewEntitiesHelper.newPayment();
            Payment payment2 = NewEntitiesHelper.newPayment();
            payment2.setId(2L);

            when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

            List<PaymentDTO> paymentDTOList = paymentGateway.listAllPayments();

            assertThat(paymentDTOList).isNotNull();
            assertThat(paymentDTOList).hasSize(2);
            assertThat(paymentDTOList.get(0).getId()).isEqualTo(payment1.getId());
            assertThat(paymentDTOList.get(1).getId()).isEqualTo(payment2.getId());
        }
    }

    @Nested
    class findByUuid {

        @Test
        public void test_find_by_uuid_success() {
            // Arrange
            String uuid = "valid-uuid";
            Payment payment = new Payment();
            payment.setUuid(uuid);
            PaymentClientDTO expectedDto = new PaymentClientDTO(payment);
            when(paymentRepository.findByUuid(uuid)).thenReturn(Optional.of(payment));

            // Act
            PaymentClientDTO result = paymentGateway.findByUuid(uuid);

            // Assert
            assertEquals(expectedDto, result);
        }

        @Test
        public void test_uuid_not_found() {
            // Arrange
            String uuid = "non-existent-uuid";
            when(paymentRepository.findByUuid(uuid)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> paymentGateway.findByUuid(uuid));
        }

        @Test
        public void test_null_or_empty_uuid() {
            // Arrange, Act & Assert
            assertThrows(NotFoundException.class, () -> paymentGateway.findByUuid(null));
            assertThrows(NotFoundException.class, () -> paymentGateway.findByUuid(""));
        }

        @Test
        public void test_conversion_to_dto_fails() {
            // Arrange
            String uuid = "valid-uuid";
            Payment payment = mock(Payment.class);
            when(paymentRepository.findByUuid(uuid)).thenReturn(Optional.of(payment));
            when(payment.toClientDTO()).thenThrow(new RuntimeException("Conversion error"));

            // Act & Assert
            assertThrows(UnknownErrorException.class, () -> paymentGateway.findByUuid(uuid));
        }
    }
}
