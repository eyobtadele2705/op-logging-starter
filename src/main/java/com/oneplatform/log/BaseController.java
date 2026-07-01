package com.oneplatform.log;

import com.oneplatform.log.annotations.LoggableOperation;
import com.oneplatform.log.dtos.LogEntry;
import com.oneplatform.log.enums.LogLevel;
import com.oneplatform.log.logger.StructuredLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BaseController {

    private final StructuredLogger structuredLogger;

    @GetMapping("/test")
    @LoggableOperation("Test")
    public String test() {
        structuredLogger.log(LogEntry.builder()
                        .level(LogLevel.INFO)
                        .layer("CONTROLLER")
                        .httpMethod("GET")
                        .direction("INCOMING")
                        .message("Test endpoint")
                        .operation("Test")
                .build());
        return "Test endpoint";
    }
}
