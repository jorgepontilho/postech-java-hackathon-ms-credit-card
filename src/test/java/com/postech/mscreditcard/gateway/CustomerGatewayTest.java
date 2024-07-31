package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootApplication
public class CustomerGatewayTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerGateway customerGateway;

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
    class InsertCustomer {
        @Test
        void shouldCreateANewCustomer() {
            Customer customer = NewEntitiesHelper.newCustomer();
            CustomerDTO customerDTO = customer.toDTO();

            when(customerRepository.save(any(Customer.class))).thenReturn(customer);

            CustomerDTO customerNew = customerGateway.createCustomer(customerDTO);

            assertThat(customerNew).isNotNull().isInstanceOf(CustomerDTO.class);
        }
    }

    @Nested
    class FindCustomerByCpf {

        @Test
        void shouldFindCustomerByCpf() {
            Customer customer = NewEntitiesHelper.newCustomer();
            when(customerRepository.findByCpf(customer.getCpf())).thenReturn(Optional.of(customer));

            CustomerDTO foundCustomer = customerGateway.findByCpf(customer.getCpf());

            assertThat(foundCustomer).isNotNull();
            assertThat(foundCustomer.getCpf()).isEqualTo(customer.getCpf());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCustomerNotFound() {
            when(customerRepository.findByCpf(anyString())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> customerGateway.findByCpf("00000000000"));
        }
    }

    @Nested
    class ListAllCustomers {

        @Test
        void shouldReturnListOfCustomerDTOs() {
            Customer customer1 = NewEntitiesHelper.newCustomer();
            Customer customer2 = NewEntitiesHelper.newCustomer();
            customer2.setId(2);
            customer2.setCpf("989898982-91");

            when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

            List<CustomerDTO> customerDTOList = customerGateway.listAllCustomers();

            assertThat(customerDTOList).isNotNull();
            assertThat(customerDTOList).hasSize(2);
            assertThat(customerDTOList.get(0).getCpf()).isEqualTo(customer1.getCpf());
            assertThat(customerDTOList.get(1).getCpf()).isEqualTo(customer2.getCpf());
        }

        @Test
        void shouldThrowUnknownErrorExceptionWhenErrorOccurs() {
            // Arrange
            when(customerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(UnknownErrorException.class, () -> customerGateway.listAllCustomers());
        }
    }

    @Nested
    class DeleteCustomerById {

        @Test
        void shouldDeleteCustomerById() {
            doNothing().when(customerRepository).deleteById(anyInt());

            customerGateway.deleteById(1);

            verify(customerRepository).deleteById(1);
        }

        @Test
        void shouldThrowUnknownErrorExceptionWhenErrorOccurs() {
            doThrow(new RuntimeException("Database error")).when(customerRepository).deleteById(anyInt());

            assertThrows(UnknownErrorException.class, () -> customerGateway.deleteById(1));
        }
    }
}
