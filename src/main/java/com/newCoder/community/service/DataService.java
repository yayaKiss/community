package com.newCoder.community.service;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-22 17:04
 * @Desc
 */
public interface DataService {
    /**
     * 统计每日uv
     */
    void recordUV(String ip);

    /**
     * 统计区间uv
     */
    long recordUV(Date start, Date end);

    /**
     * 统计每日dau
     */
    void recordDAU(int userId);

    /**
     * 统计区间dau
     */
    long recordDAU(Date start,Date end);
}
