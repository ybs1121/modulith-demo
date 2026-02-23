package com.toy.modulithdemo.shared.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();

    String value() default "";

    long waitTime() default 10L;

    long leaseTime() default 3L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}