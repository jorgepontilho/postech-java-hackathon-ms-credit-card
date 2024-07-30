package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.gateway.PaymentGateway;
import com.postech.mscreditcard.usecase.CreditCardUseCase;
import com.postech.mscreditcard.usecase.PaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentGateway paymentGateway;

    @PostMapping("/pagamentos")
    @Operation(summary = "Create a new Payment with a DTO", responses = {
            @ApiResponse(description = "The new Payment was created", responseCode = "201", content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando")))
    })
    public ResponseEntity<?> createPayment(HttpServletRequest request, @Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("PostMapping - createpayment [{}]", paymentDTO.getValor());
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            PaymentUseCase.validateCardCreationPayment(paymentDTO);
            PaymentDTO paymentCreated = paymentGateway.createPayment(paymentDTO);
            return new ResponseEntity<>(paymentCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/pagamentos")
    @Operation(summary = "Get all Payments", responses = {
            @ApiResponse(description = "List of all Payments", responseCode = "200"),
    })
    public ResponseEntity<?> listAllPayments(HttpServletRequest request) {
        log.info("GetMapping - listAllPayments");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        return new ResponseEntity<>(paymentGateway.listAllPayments(), HttpStatus.OK);
    }

}

