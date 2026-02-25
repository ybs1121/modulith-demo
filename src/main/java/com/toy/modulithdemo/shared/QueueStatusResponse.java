package com.toy.modulithdemo.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueStatusResponse {
    private String status; // SUCCESS, WAITING, NOT_FOUND_OR_SOLD_OUT
    private Long aheadCount; // 내 앞에 대기 중인 사람 수
}
