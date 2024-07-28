package com.postech.mscreditcard.controller;

import com.postech.mscreditcard.dto.UserDTO;
import com.postech.mscreditcard.entity.User;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.security.SecurityFilter;
import com.postech.mscreditcard.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

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
    public ResponseEntity autenticacao(@RequestBody @Valid UserDTO user) {
        log.info("PostMapping - Autenticacao for user [{}]", user.getUsuario());
        try {
            UserDTO userDTO = creditCardGateway.findByLoginAndPassword(user.getUsuario(), user.getSenha());
            if (userDTO == null) {
                return new ResponseEntity<>("Login ou Senha inválida.", HttpStatus.BAD_REQUEST);
            }
            String token = tokenService.generateToken(userDTO);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

