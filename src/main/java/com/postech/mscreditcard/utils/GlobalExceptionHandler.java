package com.postech.mscreditcard.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        // Obtém os detalhes dos campos com erro
        String camposComErro = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField())
                .distinct()
                .collect(Collectors.joining(", "));

        // Monta a mensagem de erro
        String mensagemErro = "Campos inválidos ou faltando: " + camposComErro;

        return ResponseEntity.badRequest().body(mensagemErro);
    }
}
