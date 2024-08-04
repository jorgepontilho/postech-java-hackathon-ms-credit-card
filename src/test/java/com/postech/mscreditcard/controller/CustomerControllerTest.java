package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CustomerGateway;
import com.postech.mscreditcard.usecase.CustomerUseCase;
import com.postech.mscreditcard.exceptions.GlobalExceptionHandler;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @InjectMocks
    private CustomerController customerController;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);

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
        public void test_customer_creation_with_valid_dto_return_http_200() throws Exception {
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

        @Test
        public void test_customer_creation_with_general_exception_return_http_400() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);
            CustomerDTO customerDTO = new CustomerDTO();

            when(customerUseCase.canCreateCustomer(any())).thenThrow(new RuntimeException("Any error", null));
            ResponseEntity<?> response = customerController.createCustomer(request, customerDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void test_customer_creation_with_unauthenticated_user_return_http_401() throws Exception {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCpf(VALID_CPF);
            customerDTO.setNome("moe na");
            customerDTO.setEmail("m@example.com");
            customerDTO.setTelefone("1234567890");

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = customerController.createCustomer(request, customerDTO);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        public void test_customer_creation_with_internal_error_return_http_500() throws Exception {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCpf(VALID_CPF);
            customerDTO.setNome("moe na");
            customerDTO.setEmail("m@example.com");
            customerDTO.setTelefone("1234567890");

            when(customerUseCase.canCreateCustomer(any(CustomerDTO.class))).thenReturn(true);
            when(customerGateway.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

            mockMvc.perform(post("/api/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(customerDTO)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        public void test_customer_creation_with_existing_customer_return_http_500() throws Exception {
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
        public void test_customer_creation_with_unknown_exception_return_http_500() throws Exception {
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

    @Nested
    class ListAllCustomers {
        @Test
        public void test_list_all_customers_return_http_200(){
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            List<CustomerDTO> customers = Arrays.asList(new CustomerDTO(), new CustomerDTO());
            when(customerGateway.listAllCustomers()).thenReturn(customers);

            ResponseEntity<?> response = customerController.listAllCustomers(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void test_list_all_customers_with_general_exception_return_http_400() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            when(customerGateway.listAllCustomers()).thenThrow(new RuntimeException("Generic error"));

            ResponseEntity<?> response = customerController.listAllCustomers(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Generic error", response.getBody());
        }

        @Test
        public void test_list_all_customers_with_unauthenticated_user_return_http_401() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = customerController.listAllCustomers(request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        public void test_list_all_customers_with_unknown_exception_return_http_500() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            when(customerGateway.listAllCustomers()).thenThrow(new UnknownErrorException("Unknown error", new Exception()));

            ResponseEntity<?> response = customerController.listAllCustomers(request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unknown error", response.getBody());
        }
    }

    @Nested
    class DeleteCustomer {
        @Test
        public void test_delete_customer_return_http_200() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            CustomerGateway customerGateway = mock(CustomerGateway.class);
            CustomerController controller = new CustomerController(null, customerGateway);

            ResponseEntity<?> response = controller.deleteCustomer(request, 1);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void test_delete_customer_with_general_exception_return_http_400() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            CustomerGateway customerGateway = mock(CustomerGateway.class);

            doThrow(new RuntimeException("Generic error", new Exception())).when(customerGateway).deleteById(1);
            CustomerController controller = new CustomerController(null, customerGateway);

            ResponseEntity<?> response = controller.deleteCustomer(request, 1);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Generic error", response.getBody());
        }

        @Test
        public void test_delete_customer_with_unauthenticated_user_return_http_401() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn("Usuário não autenticado");
            when(request.getAttribute("error_code")).thenReturn(HttpStatus.UNAUTHORIZED);

            ResponseEntity<?> response = customerController.deleteCustomer(request, 1);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Usuário não autenticado", response.getBody());
        }

        @Test
        public void test_delete_customer_with_unknown_exception_return_http_500() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getAttribute("error")).thenReturn(null);
            CustomerGateway customerGateway = mock(CustomerGateway.class);

            doThrow(new UnknownErrorException("Unknown error", new Exception())).when(customerGateway).deleteById(1);
            CustomerController controller = new CustomerController(null, customerGateway);

            ResponseEntity<?> response = controller.deleteCustomer(request, 1);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Unknown error", response.getBody());
        }
    }

}