package com.oneplatform.log.autoconfig;

import com.oneplatform.log.aspects.OperationLoggingAspect;
import com.oneplatform.log.filter.TraceFilter;
import com.oneplatform.log.logger.StructuredLogger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(OpLoggingProperties.class)
public class OpLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StructuredLogger structuredLogger() {
        return new StructuredLogger();
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationLoggingAspect operationLoggingAspect(StructuredLogger structuredLogger) {
        return new OperationLoggingAspect(structuredLogger);
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
        FilterRegistrationBean<TraceFilter> bean = new FilterRegistrationBean<>(new TraceFilter());
        bean.setOrder(1);
        return bean;
    }
}