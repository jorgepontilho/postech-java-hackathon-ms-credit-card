package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CustomerGateway;
import com.postech.mscreditcard.usecase.CustomerUseCase;
import com.postech.mscreditcard.exceptions.GlobalExceptionHandler;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootApplication
public class CustomerControllerTest {

    public static final String VALID_CPF = "51958902004";
    private MockMvc mockMvc;

    @Mock
    private CustomerUseCase customerUseCase;

    @Mock
    private CustomerGateway customerGateway;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        CustomerController customerController = new CustomerController(customerUseCase, customerGateway);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CreateCustomer {

        @Test
        void shouldCreateCustomer() throws Exception {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCpf(VALID_CPF);
            customerDTO.setNome("moe na");
            customerDTO.setEmail("m@example.com");
            customerDTO.setTelefone("1234567890");
            customerDTO.setCidade("Some City");
            customerDTO.setRua("mirabeau");
            customerDTO.setEstado("AA");
            customerDTO.setCep("01502001");
            customerDTO.setPais("FR");

            when(customerUseCase.canCreateCustomer(any(CustomerDTO.class))).thenReturn(true);
            when(customerGateway.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

            mockMvc.perform(post("/api/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(customerDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(NewEntitiesHelper.asJsonString(customerDTO)));
        }
    }

    @Test
    void shouldReturnBadRequestWhenCustomerExists() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCpf(VALID_CPF);
        customerDTO.setNome("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setTelefone("1234567890");
        customerDTO.setRua("123 Main St");
        customerDTO.setCidade("Some City");
        customerDTO.setEstado("SP");
        customerDTO.setCep("12345-678");
        customerDTO.setPais("BR");

        when(customerUseCase.canCreateCustomer(any(CustomerDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NewEntitiesHelper.asJsonString(customerDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Cliente já existe."));
    }

    @Test
    void shouldReturnInternalServerErrorOnUnknownError() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCpf(VALID_CPF);
        customerDTO.setNome("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setTelefone("1234567890");
        customerDTO.setRua("123 Main St");
        customerDTO.setCidade("Some City");
        customerDTO.setEstado("SP");
        customerDTO.setCep("12345-678");
        customerDTO.setPais("BR");

        when(customerUseCase.canCreateCustomer(any())).thenThrow(new UnknownErrorException("Unknown error", null));

        mockMvc.perform(post("/api/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NewEntitiesHelper.asJsonString(customerDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unknown error"));
    }
}