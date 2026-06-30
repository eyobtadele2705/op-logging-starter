package com.oneplatform.log.logger;


import com.oneplatform.log.dtos.LogEntry;
import com.oneplatform.log.utils.SensitiveDataMasker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StructuredLogger {

    private static final Logger log = LoggerFactory.getLogger("APP_LOG");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_PAYLOAD_LEN = 1000;

    public void log(LogEntry entry) {
        String line = format(entry);

        switch (entry.getLevel()) {
            case TRACE -> log.trace(line, entry.getError());
            case DEBUG -> log.debug(line, entry.getError());
            case INFO  -> log.info(line, entry.getError());
            case WARN  -> log.warn(line, entry.getError());
            case ERROR -> log.error(line, entry.getError());
        }
    }

    private String format(LogEntry e) {
        StringBuilder sb = new StringBuilder();
        sb.append("[op=").append(e.getOperation()).append("]")
                .append("[layer=").append(e.getLayer()).append("]")
                .append("[dir=").append(e.getDirection()).append("] ");

        if (e.getHttpMethod() != null) {
            sb.append(e.getHttpMethod()).append(" ").append(e.getUri()).append(" ");
        }
        if (e.getStatusCode() != null) {
            sb.append("status=").append(e.getStatusCode()).append(" ");
        }
        if (e.getDurationMs() != null) {
            sb.append("duration=").append(e.getDurationMs()).append("ms ");
        }
        if (e.getMessage() != null) {
            sb.append("- ").append(e.getMessage()).append(" ");
        }
        if (e.getMeta() != null && !e.getMeta().isEmpty()) {
            sb.append("meta=").append(safeJson(e.getMeta())).append(" ");
        }
        if (e.getRequestPayload() != null) {
            sb.append("request=").append(truncate(safeJson(e.getRequestPayload()))).append(" ");
        }
        if (e.getResponsePayload() != null) {
            sb.append("response=").append(truncate(safeJson(e.getResponsePayload())));
        }
        return sb.toString();
    }

    private String safeJson(Object obj) {
        try {
            return SensitiveDataMasker.maskJson(obj);
//            return MAPPER.writeValueAsString(obj);
        } catch (Exception ex) {
            return "[unserializable: " + obj.getClass().getSimpleName() + "]";
        }
    }

    private String truncate(String s) {
        return s.length() > MAX_PAYLOAD_LEN ? s.substring(0, MAX_PAYLOAD_LEN) + "...[truncated]" : s;
    }
}