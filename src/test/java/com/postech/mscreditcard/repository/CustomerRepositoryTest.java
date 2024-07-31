package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = org.mockito.MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void shouldCreateANewCustomer() {

        Customer customer = NewEntitiesHelper.newCustomer();
        int id = customer.getId();
        customerRepository.save(customer);

        when(customerRepository.save(customer)).thenReturn(customer);

        assertThat(customer.getId()).isEqualTo(id);
    }

    @Test
    void shouldFindByCpf() {

        Customer customer = NewEntitiesHelper.newCustomer();
        int id = customer.getId();
        customerRepository.save(customer);

        when(customerRepository.findByCpf(customer.getCpf())).thenReturn(Optional.of(customer));

        Optional<Customer> customerFound = customerRepository.findByCpf(customer.getCpf());

        assertThat(customerFound.isPresent()).isTrue();
        assertThat(customerFound.get()).isEqualTo(customer);
        assertThat(customerFound.get().getId()).isEqualTo(id);
    }
}
