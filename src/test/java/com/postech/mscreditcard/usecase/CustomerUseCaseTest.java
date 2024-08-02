package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.gateway.CustomerGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootApplication
public class CustomerUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @InjectMocks
    private CustomerUseCase customerUseCase;

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
    class CanCreateCustomer {

        @Test
        void shouldReturnTrueWhenCustomerNotFound() {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCpf("194693002-19");

            when(customerGateway.findByCpf(anyString())).thenThrow(new NotFoundException("Cliente não encontrado"));

            boolean result = customerUseCase.canCreateCustomer(customerDTO);

            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalseWhenCustomerExists() {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCpf("12345678900");
            Customer customer = new Customer();
            customer.setCpf("12345678900");

            when(customerGateway.findByCpf(anyString())).thenReturn(customer.toDTO());

            boolean result = customerUseCase.canCreateCustomer(customerDTO);

            assertThat(result).isFalse();
        }

        //@Test
        //        void shouldThrowUnknownErrorExceptionWhenUnknownErrorOccurs() {
        //            // Arrange
        //            CustomerDTO customerDTO = new CustomerDTO();
        //            customerDTO.setCpf("12345678900");
        //
        //            when(customerGateway.findByCpf(anyString())).thenThrow(new RuntimeException("Unknown error"));
        //            // Act & Assert
        //            assertThrows(UnknownErrorException.class, () -> customerUseCase.canCreateCustomer(customerDTO));
        //        }
    }
}