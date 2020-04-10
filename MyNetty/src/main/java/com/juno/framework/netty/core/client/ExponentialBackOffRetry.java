package com.juno.framework.netty.core.client;

import com.juno.framework.netty.exception.NettyFwException;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @Author: Juno
 * @Date: 2020/4/9 15:09
 */
@Slf4j
public class ExponentialBackOffRetry implements RetryPolicy {

    private static final int MAX_RETRIES_LIMIT = 29;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;

    private final Random random = new Random();
    private final long baseSleepTimeMs;
    private final int maxRetries;
    private final int maxSleepMs;

    public ExponentialBackOffRetry(int baseSleepTimeMs, int maxRetries) {
        this(baseSleepTimeMs, maxRetries, DEFAULT_MAX_SLEEP_MS);
    }

    public ExponentialBackOffRetry(int baseSleepTimeMs, int maxRetries, int maxSleepMs) {
        this.maxRetries = maxRetries;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxSleepMs = maxSleepMs;
    }

    @Override
    public boolean allowRetry(int retryCount) {
        return retryCount < maxRetries;
    }

    @Override
    public long getSleepTimeMs(int retryCount) {
        if (retryCount < 0) {
            throw new NettyFwException("retries count must greater than 0.");
        }
        if (retryCount > MAX_RETRIES_LIMIT) {
            log.info("maxRetries too large ({}). Pinning to {}", maxRetries, MAX_RETRIES_LIMIT);
            retryCount = MAX_RETRIES_LIMIT;
        }
        long sleepMs = baseSleepTimeMs * Math.max(1, random.nextInt(1 << retryCount));
        if (sleepMs > maxSleepMs) {
            log.info("Sleep extension too large ({}). Pinning to {}", sleepMs, maxSleepMs);
            sleepMs = maxSleepMs;
        }
        return sleepMs;
    }
}
