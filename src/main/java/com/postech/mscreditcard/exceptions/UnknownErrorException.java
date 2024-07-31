package com.postech.mscreditcard.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unknown server error")
public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }

    public UnknownErrorException() {
        super();
    }
}
