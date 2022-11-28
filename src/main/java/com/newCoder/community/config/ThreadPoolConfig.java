package com.newCoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lijie
 * @date 2022-11-26 20:06
 * @Desc
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
