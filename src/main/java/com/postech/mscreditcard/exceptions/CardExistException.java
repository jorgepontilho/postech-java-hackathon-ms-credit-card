package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Número de Cartão já existe.")
public class CardExistException extends RuntimeException {

    public CardExistException(String s) {
        super(s);
    }
}
