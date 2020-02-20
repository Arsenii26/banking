package com.userfront.exceptions;

import org.springframework.http.HttpStatus;

public class NegativeBalanceException extends RuntimeException {
    private final String msgCode;
    private final HttpStatus httpStatus;

    public NegativeBalanceException(String msgCode, HttpStatus httpStatus) {
        super(String.format(msgCode));
        this.msgCode = msgCode;
        this.httpStatus = httpStatus;
    }
}
