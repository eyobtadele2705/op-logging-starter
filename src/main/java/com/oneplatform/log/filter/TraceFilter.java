package com.oneplatform.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class TraceFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER  = "X-Request-Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        // MDC must be populated FIRST before any log statement
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        // Now traceId will appear correctly in %X{traceId} for every line below
        log.info("\nIncoming request: \n{} {} \ntraceId={}\n", request.getMethod(), request.getRequestURI(), traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clean up — prevents traceId leaking into the next
            // request handled by the same thread (thread pool reuse)
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}