package com.toy.modulithdemo.promotion.exception;

import com.toy.modulithdemo.promotion.constant.PromotionErrorCode;
import com.toy.modulithdemo.user.constant.UserErrorCode;
import lombok.Getter;

@Getter
public class PromotionException extends RuntimeException {
    private final PromotionErrorCode errorCode;

    public PromotionException(PromotionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
