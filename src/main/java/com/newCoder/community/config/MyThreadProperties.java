package com.newCoder.community.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lijie
 * @date 2022-11-11 14:11
 * @Desc
 */
@Data
@Component
@ConfigurationProperties(prefix = "community.thread")
public class MyThreadProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveTime;
}
