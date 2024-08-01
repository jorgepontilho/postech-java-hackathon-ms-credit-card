package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.GlobalExceptionHandler;
import com.postech.mscreditcard.gateway.PaymentGateway;
import com.postech.mscreditcard.usecase.PaymentUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootApplication
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentUseCase paymentUseCase;

    @Mock
    private PaymentGateway paymentGateway;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        PaymentController paymentController = new PaymentController(paymentGateway, paymentUseCase);

        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CreatePayment {

        @Test
        void shouldCreatePayment() throws Exception {
            PaymentDTO paymentDTO = NewEntitiesHelper.newPayment().toDTO();

            when(paymentGateway.createPayment(any(PaymentDTO.class))).thenReturn(paymentDTO);

            mockMvc.perform(post("/api/pagamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(NewEntitiesHelper.asJsonString(paymentDTO)))
                    .andExpect(status().isOk());
        }
    }
}