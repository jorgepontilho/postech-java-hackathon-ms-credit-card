package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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

        @Test
        public void throws_illegalargumentexception_when_customerdto_is_null() {
            // Arrange
            CustomerGateway customerGateway = mock(CustomerGateway.class);
            CustomerUseCase customerService = new CustomerUseCase(customerGateway);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                customerService.canCreateCustomer(null);
            });
        }

        // Handles UnknownErrorException correctly when an unknown error occurs
        @Test
        public void handles_unknownerrorexception_correctly_when_unknown_error_occurs() {
            // Arrange
            CustomerGateway customerGateway = mock(CustomerGateway.class);
            CustomerDTO customerDTO = new CustomerDTO(1L, "12345678901", "John Doe", "john.doe@example.com", "1234567890", "Street 1", "City", "ST", "12345-678", "Country");
            CustomerUseCase customerService = new CustomerUseCase(customerGateway);
            when(customerGateway.findByCpf(customerDTO.getCpf())).thenThrow(new UnknownErrorException("Unknown error",new UnknownErrorException()));

            // Act & Assert
            assertThrows(UnknownErrorException.class, () -> {
                customerService.canCreateCustomer(customerDTO);
            });
        }

    }
}