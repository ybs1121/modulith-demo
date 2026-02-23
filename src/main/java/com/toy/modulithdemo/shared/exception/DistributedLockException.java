package com.toy.modulithdemo.shared.exception;

import java.util.concurrent.TimeUnit;

public class DistributedLockException extends RuntimeException {

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DistributedLockException timeout(String lockKey, long waitTime, TimeUnit timeUnit) {
        return new DistributedLockException(
                String.format("분산락 획득 대기 시간 초과 - Key: %s, 대기시간: %d %s", lockKey, waitTime, timeUnit)
        );
    }

    public static DistributedLockException interrupted(String lockKey, Throwable cause) {
        return new DistributedLockException(
                String.format("분산락 획득 중 인터럽트 발생 - Key: %s", lockKey), cause
        );
    }
}
