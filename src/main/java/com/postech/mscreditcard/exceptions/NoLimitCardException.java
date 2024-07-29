package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PAYMENT_REQUIRED, reason = "Cartão sem limite")
public class NoLimitCardException extends RuntimeException {
    public NoLimitCardException(String s) {
        super(s);
    }
}
