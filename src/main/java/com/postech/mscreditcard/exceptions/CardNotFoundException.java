package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Cartão não encontrado")
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String s) {
        super(s);
    }
}
