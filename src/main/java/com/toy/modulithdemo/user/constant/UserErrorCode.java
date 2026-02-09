package com.toy.modulithdemo.user.constant;

import lombok.Getter;

@Getter
public enum UserErrorCode {
    INVALID_AUTH_INPUT("USR00001", "올바르지 않는 아이디 혹은 패스워드입니다. "),
    NOT_FOUND("USR00002", "사용자를 찾을 수 없습니다."),

    ;

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
