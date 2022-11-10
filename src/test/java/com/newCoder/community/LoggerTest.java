package com.newCoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lijie
 * @date 2022-11-10 11:23
 * @Desc
 */
@SpringBootTest
public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
    }

}
