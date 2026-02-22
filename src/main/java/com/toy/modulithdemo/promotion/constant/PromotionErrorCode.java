package com.toy.modulithdemo.promotion.constant;

import lombok.Getter;

@Getter
public enum PromotionErrorCode {

    NOT_FOUND("PRO00001", "프로모션을 찾을 수 없습니다."),
    ALL_USED_UP("PRO00001", "쿠폰이 모두 소진되었습니다."),

    ;

    private final String code;
    private final String message;

    PromotionErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
