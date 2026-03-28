package com.bookstore.global.exception.auth;

import com.bookstore.global.exception.BusinessException;
import com.bookstore.global.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
