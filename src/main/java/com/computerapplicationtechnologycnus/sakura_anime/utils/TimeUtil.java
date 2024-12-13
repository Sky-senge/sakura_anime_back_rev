package com.computerapplicationtechnologycnus.sakura_anime.utils;

import java.time.Instant;

public class TimeUtil {
    /**
     * 获取当前秒级 Timestap
     * @return
     */
    public static long getCurrentTimestampInSeconds() {
        return Instant.now().getEpochSecond();
    }
}