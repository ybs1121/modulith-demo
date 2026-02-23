package com.toy.modulithdemo.shared.aop;


import com.toy.modulithdemo.shared.annotation.DistributedLock;
import com.toy.modulithdemo.shared.exception.DistributedLockException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ConcurrentHashMap<String, Expression> expressionCache = new ConcurrentHashMap<>();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String LOCK_PREFIX = "lock:";

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockName = generateLockName(distributedLock, joinPoint);
        RLock acquiredLock = acquireLock(lockName, distributedLock);

        try {
            log.info("분산락 획득 완료 - Key: {}", lockName);
            return joinPoint.proceed();
        } finally {
            releaseLock(acquiredLock, lockName);
        }
    }

    private String generateLockName(DistributedLock distributedLock, ProceedingJoinPoint joinPoint) {
        String targetKey = distributedLock.key();
        if (StringUtils.isBlank(targetKey)) {
            throw new DistributedLockException("분산락 Key가 정의되지 않았습니다.");
        }

        String identifier = distributedLock.value();
        if (StringUtils.isBlank(identifier)) {
            return LOCK_PREFIX + targetKey;
        }

        return LOCK_PREFIX + targetKey + ":" + parseSpelExpression(identifier, joinPoint);
    }

    private String parseSpelExpression(String spelExpression, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // MethodSignature의 getParameterNames()가 null을 반환하는 버그 방어 로직
        String[] paramNames = signature.getParameterNames();
        if (paramNames == null) {
            paramNames = parameterNameDiscoverer.getParameterNames(method);
        }


        Object[] args = joinPoint.getArgs();
        Expression expression = expressionCache.computeIfAbsent(spelExpression, parser::parseExpression);
        EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }


        Object parsedValue = expression.getValue(context);
        return parsedValue != null ? parsedValue.toString() : "";
    }

    private RLock acquireLock(String lockName, DistributedLock distributedLock) {
        RLock lock = redissonClient.getLock(lockName);

        try {
            boolean isAcquired = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!isAcquired) {
                log.warn("분산락 획득 대기 시간 초과 - Key: {}", lockName);
                throw DistributedLockException.timeout(lockName, distributedLock.waitTime(), distributedLock.timeUnit());
            }

            return lock;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("분산락 획득 중 인터럽트 발생 - Key: {}", lockName, e);
            throw DistributedLockException.interrupted(lockName, e);
        }
    }

    private void releaseLock(RLock lock, String lockName) {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.info("분산락이 이미 해제되었거나 소유권이 없습니다 - Key: {}", lockName);
        } catch (Exception e) {
            log.error("분산락 해제 중 시스템 오류 발생 - Key: {}", lockName, e);
        }
    }
}
