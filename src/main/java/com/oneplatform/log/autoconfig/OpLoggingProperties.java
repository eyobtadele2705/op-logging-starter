package com.oneplatform.log.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "op.logging")
public class OpLoggingProperties {
    private boolean maskingEnabled = true;
    private int maxPayloadLength = 1000;
    // services can override via application.yml: op.logging.max-payload-length=2000
}