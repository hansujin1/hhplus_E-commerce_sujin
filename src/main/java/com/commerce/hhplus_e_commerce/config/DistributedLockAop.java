package com.commerce.hhplus_e_commerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final DistributedLockKeyGenerator keyGenerator;

    @Around("@annotation(com.commerce.hhplus_e_commerce.config.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // KeyGenerator를 통해 동적으로 key 생성
        String lockKey = keyGenerator.generate(signature, joinPoint.getArgs(), distributedLock.key());
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!available) {
                log.warn("Lock 획득 실패: {}", lockKey);
                throw new IllegalStateException("Lock을 획득하지 못했습니다.");
            }

            log.info("Lock 획득 성공: {}", lockKey);
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock 획득 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock 해제: {}", lockKey);
            }
        }
    }
}
