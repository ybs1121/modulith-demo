package com.toy.modulithdemo.user.exception;

import com.toy.modulithdemo.order.constant.OrderErrorCode;
import com.toy.modulithdemo.user.constant.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
