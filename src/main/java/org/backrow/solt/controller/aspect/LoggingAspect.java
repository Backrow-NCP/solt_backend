package org.backrow.solt.controller.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class LoggingAspect {
    // 포인트컷: 패키지 내 모든 메서드를 대상으로 로깅
    @Pointcut("execution(* org.backrow.solt.service..*(..))")
    public void applicationPackagePointcut() {}

    // 메서드 실행 전 로깅
    @Before("applicationPackagePointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // 메서드 정상 실행 후 로깅
    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned: {}", joinPoint.getSignature().toShortString(), result);
    }

    // 예외 발생 시 로깅
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {} threw exception: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    // 메서드 실행 전후로 로깅 (성능 측정 등)
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: {}", joinPoint.getSignature().toShortString());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Exception in method: {}", joinPoint.getSignature().toShortString());
            throw throwable;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), timeTaken);

        return result;
    }
}
