package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Problema nos dados de pagamento")
public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String s) {
        super(s);
    }
}
