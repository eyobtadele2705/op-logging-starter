package com.oneplatform.log.dtos;

import com.oneplatform.log.enums.LogLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class LogEntry {

    private LogLevel level;          // which level this entry should be logged at
    private String traceId;          // request-scoped correlation id
    private String operation;        // business objective, e.g. "LOGIN", "DEVICE_SALES_FETCH"
    private String layer;            // CONTROLLER / SERVICE / REPOSITORY / CLIENT
    private String direction;        // INCOMING / OUTGOING / INTERNAL
    private String httpMethod;       // GET/POST/etc, null for non-HTTP layers
    private String uri;
    private Integer statusCode;
    private Object requestPayload;   // will be masked/truncated before printing
    private Object responsePayload;
    private Long durationMs;
    private String message;          // free-text summary, e.g. "Validating credentials"
    private Throwable error;
    private Map<String, Object> meta; // anything extra: clusterId, agentId, role, etc.
}