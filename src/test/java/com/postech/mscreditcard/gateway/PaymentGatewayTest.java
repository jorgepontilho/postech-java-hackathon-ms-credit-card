package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.repository.CreditCardRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PaymentGatewayTest {

    @Mock
    private PaymentRepository paymentRepository;

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

            when(paymentRepository.save(payment)).thenAnswer(invocation -> invocation.getArgument(0));

            PaymentDTO createdPayment = paymentGateway.createPayment(paymentDTO);

            assertThat(createdPayment).isNotNull();
            assertThat(createdPayment.getId()).isEqualTo(paymentDTO.getId());
        }
    }

    @Nested
    class ListAllPayments {

        @Test
        void shouldReturnListOfPaymentDTOs() {
            Payment payment1 = NewEntitiesHelper.newPayment();
            Payment payment2 = NewEntitiesHelper.newPayment();
            payment2.setId(2);

            when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

            List<PaymentDTO> paymentDTOList = paymentGateway.listAllPayments();

            assertThat(paymentDTOList).isNotNull();
            assertThat(paymentDTOList).hasSize(2);
            assertThat(paymentDTOList.get(0).getId()).isEqualTo(payment1.getId());
            assertThat(paymentDTOList.get(1).getId()).isEqualTo(payment2.getId());
        }
    }
}
