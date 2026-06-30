package com.oneplatform.log.aspects;

import com.oneplatform.log.annotations.LoggableOperation;
import com.oneplatform.log.dtos.LogEntry;
import com.oneplatform.log.enums.LogLevel;
import com.oneplatform.log.logger.StructuredLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OperationLoggingAspect {

    public static final String OPERATION_MDC_KEY = "operation";
    private final StructuredLogger structuredLogger;

    public OperationLoggingAspect(StructuredLogger structuredLogger) {
        this.structuredLogger = structuredLogger;
    }

    @Around("@annotation(loggableOperation)")
    public Object around(ProceedingJoinPoint pjp, LoggableOperation loggableOperation) throws Throwable {
        String operation = loggableOperation.value();
        String previous = MDC.get(OPERATION_MDC_KEY);
        MDC.put(OPERATION_MDC_KEY, operation);

        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();

            structuredLogger.log(LogEntry.builder()
                    .level(LogLevel.INFO)
                    .operation(operation)
                    .layer("CONTROLLER")
                    .direction("OUTGOING")
                    .durationMs(System.currentTimeMillis() - start)
                    .message(operation + " completed successfully")
                    .build());

            return result;
        } catch (Exception ex) {
            structuredLogger.log(LogEntry.builder()
                    .level(LogLevel.ERROR)
                    .operation(operation)
                    .layer("CONTROLLER")
                    .direction("OUTGOING")
                    .durationMs(System.currentTimeMillis() - start)
                    .message(operation + " failed")
                    .error(ex)
                    .build());
            throw ex;
        } finally {
            if (previous != null) {
                MDC.put(OPERATION_MDC_KEY, previous); // restore for nested calls, rare but safe
            } else {
                MDC.remove(OPERATION_MDC_KEY);
            }
        }
    }
}