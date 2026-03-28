package com.bookstore.global.exception.auth;

import com.bookstore.global.exception.BusinessException;
import com.bookstore.global.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
