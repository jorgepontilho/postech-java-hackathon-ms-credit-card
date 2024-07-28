package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CustomerGateway;
import com.postech.mscreditcard.security.SecurityFilter;
import com.postech.mscreditcard.security.TokenService;
import com.postech.mscreditcard.usecase.CustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerController {

    @Setter
    @Autowired
    private TokenService tokenService;

    @Setter
    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private CustomerUseCase customerUseCase;

    private final CustomerGateway customerGateway;


    @PostMapping("/cliente")
    @Operation(summary = "Create a new Customer with a DTO", responses = {
            @ApiResponse(description = "The new Customer was created", responseCode = "201", content = @Content(schema = @Schema(implementation = Customer.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
            @ApiResponse(description = "Not authenticated", responseCode = "401", content = @Content(schema = @Schema(type = "string", example = "Usuário não autenticado"))),
            @ApiResponse(description = "Server Error", responseCode = "500", content = @Content(schema = @Schema(type = "string", example = "Erro inesperado")))
    })
    public ResponseEntity<?> createCustomer(HttpServletRequest request, @Valid @RequestBody CustomerDTO customerDTO) {
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }

        log.info("PostMapping - createCustomer [{}]", customerDTO.getCpf());
        try {
            if (customerUseCase.canCreateCustomer(customerDTO)) {
                CustomerDTO customerCreated = customerGateway.createCustomer(customerDTO);
                return new ResponseEntity<>(customerCreated, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Cliente já existe.", HttpStatus.BAD_REQUEST);
            }

        } catch (UnknownErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/cliente")
    @Operation(summary = "Get all Customers", responses = {
            @ApiResponse(description = "List of all cutomers", responseCode = "200"),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
            @ApiResponse(description = "Not authenticated", responseCode = "401", content = @Content(schema = @Schema(type = "string", example = "Usuário não autenticado"))),
            @ApiResponse(description = "Server Error", responseCode = "500", content = @Content(schema = @Schema(type = "string", example = "Erro inesperado")))
    })
    public ResponseEntity<?> listAllCustomers(HttpServletRequest request) {
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            log.info("GetMapping - listAllCustomer");
            List<CustomerDTO> allCustomers = customerGateway.listAllCustomers();
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        } catch (UnknownErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/cliente/{id}")
    @Operation(summary = "Delete a Customers", responses = {
            @ApiResponse(description = "Delete a customer", responseCode = "200"),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
            @ApiResponse(description = "Not authenticated", responseCode = "401", content = @Content(schema = @Schema(type = "string", example = "Usuário não autenticado"))),
            @ApiResponse(description = "Server Error", responseCode = "500", content = @Content(schema = @Schema(type = "string", example = "Erro inesperado")))
    })
    public ResponseEntity<?> deleteCustomer(HttpServletRequest request, @PathVariable @Valid Long id) {
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            log.info("DeleteMapping - delete customer {}", id);
            customerGateway.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnknownErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

