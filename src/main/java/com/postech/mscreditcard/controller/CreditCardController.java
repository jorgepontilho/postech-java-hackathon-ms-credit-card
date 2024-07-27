package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.*;
import com.postech.mscreditcard.entity.*;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.security.SecurityFilter;
import com.postech.mscreditcard.usecase.CreditCardUseCase;
import com.postech.mscreditcard.security.TokenService;
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

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditCardController {

    @Setter
    @Autowired
    private TokenService tokenService;

    @Setter
    @Autowired
    private SecurityFilter securityFilter;

    private final CreditCardGateway creditCardGateway;

    @PostMapping("/autenticacao")
    @Operation(summary = "Get Token by Login and Password", responses = {
            @ApiResponse(description = "The Token by login", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Login ou senha inválida.")))
    })
    public ResponseEntity login(@RequestBody @Valid UserDTO user) {
        log.info("PostMapping - Login for user [{}]", user.getLogin());
        try {
            UserDTO userDTO = creditCardGateway.findByLoginAndPassword(user.getLogin(), user.getPassword());
            if (userDTO == null) {
                return new ResponseEntity<>("Login ou Senha inválida.", HttpStatus.BAD_REQUEST);
            }
            String token = tokenService.generateToken(userDTO);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/cliente")
    @Operation(summary = "Create a new Customer with a DTO", responses = {
            @ApiResponse(description = "The new Customer was created", responseCode = "201", content = @Content(schema = @Schema(implementation = Customer.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando")))
    })
    public ResponseEntity<?> createCustomer(HttpServletRequest request, @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("PostMapping - createCustomer [{}]", customerDTO.getNome());
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            CreditCardUseCase.validarCliente(customerDTO);
            if (creditCardGateway.findByCpf(customerDTO.getCpf()) != null) {
                return new ResponseEntity<>("Cliente já existe.", HttpStatus.BAD_REQUEST);
            }
            CustomerDTO customerCreated = creditCardGateway.createCustomer(customerDTO);
            return new ResponseEntity<>(customerCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/cartao")
    @Operation(summary = "Create a new Card with a DTO", responses = {
            @ApiResponse(description = "The new Card was created", responseCode = "201", content = @Content(schema = @Schema(implementation = Card.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando")))
    })
    public ResponseEntity<?> createCard(HttpServletRequest request, @Valid @RequestBody CardDTO cardDTO) {
        log.info("PostMapping - createCard [{}]", cardDTO.getNumero());
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            CreditCardUseCase.validarCartao(cardDTO);
            CardDTO cardCreated = creditCardGateway.createCard(cardDTO);
            return new ResponseEntity<>(cardCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/pagamentos")
    @Operation(summary = "Create a new Payment with a DTO", responses = {
            @ApiResponse(description = "The new Payment was created", responseCode = "201", content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando")))
    })
    public ResponseEntity<?> createpayment(HttpServletRequest request, @Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("PostMapping - createpayment [{}]", paymentDTO.getValor());
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            CreditCardUseCase.validarPagamento(paymentDTO);
            PaymentDTO paymentCreated = creditCardGateway.createPayment(paymentDTO);
            return new ResponseEntity<>(paymentCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/cliente")
    @Operation(summary = "Get all Customers", responses = {
            @ApiResponse(description = "List of all cutomers", responseCode = "200"),
    })
    public ResponseEntity<?> listAllCustomers(HttpServletRequest request) {
        log.info("GetMapping - listAllCustomer");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        return new ResponseEntity<>(creditCardGateway.listAllCustomers(), HttpStatus.OK);
    }

    @GetMapping("/cartao")
    @Operation(summary = "Get all Cards", responses = {
            @ApiResponse(description = "List of all Cards", responseCode = "200"),
    })
    public ResponseEntity<?> listAllCards(HttpServletRequest request) {
        log.info("GetMapping - listAllCards");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        return new ResponseEntity<>(creditCardGateway.listAllCards(), HttpStatus.OK);
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
        return new ResponseEntity<>(creditCardGateway.listAllPayments(), HttpStatus.OK);
    }

}

