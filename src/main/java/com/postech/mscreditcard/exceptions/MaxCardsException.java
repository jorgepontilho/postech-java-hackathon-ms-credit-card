package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Máximo de cartões atingido")
public class MaxCardsException extends RuntimeException {

    public MaxCardsException() {

    }
}
