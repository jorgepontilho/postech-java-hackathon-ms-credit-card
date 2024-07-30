package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.*;
import com.postech.mscreditcard.entity.*;
import com.postech.mscreditcard.exceptions.MaxCardsException;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.usecase.CreditCardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Autowired
    private CreditCardUseCase creditCardUseCase;

    private final CreditCardGateway creditCardGateway;

    @PostMapping("/cartao")
    @Operation(summary = "Create a new CreditCard with a DTO", responses = {
            @ApiResponse(description = "The new CreditCard was created", responseCode = "201", content = @Content(schema = @Schema(implementation = CreditCard.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
            @ApiResponse(description = "Not authenticated", responseCode = "401", content = @Content(schema = @Schema(type = "string", example = "Usuário não autenticado"))),
            @ApiResponse(description = "Server Error", responseCode = "500", content = @Content(schema = @Schema(type = "string", example = "Erro inesperado")))
    })
    public ResponseEntity<?> createCard(HttpServletRequest request, @Valid @RequestBody CreditCardDTO creditCardDTO) {
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        log.info("PostMapping - createCard [{}]", creditCardDTO.getCpf());
        try {
                creditCardUseCase.validateCardCreation(creditCardDTO);
                CreditCardDTO cardCreated = creditCardGateway.createCard(creditCardDTO);
                return new ResponseEntity<>(cardCreated, HttpStatus.CREATED);

        } catch (MaxCardsException me) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(me.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    //TODO delete e/ou update
}

