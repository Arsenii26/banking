package com.userfront.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class OverlimitException extends RuntimeException {
    private final String msgCode;
    private final HttpStatus httpStatus;


    public OverlimitException(String msgCode, HttpStatus httpStatus) {
        super(String.format(msgCode));
        this.msgCode = msgCode;
        this.httpStatus = httpStatus;
    }
}
