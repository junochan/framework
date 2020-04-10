package com.juno.framework.netty.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: Juno
 * @Date: 2020/4/1 9:30
 */
@Data
@ConfigurationProperties(prefix = "my-netty")
public class NettyProperties {

    private int port = 18989;

    private int bossThread = 2;

    private int workerThread = 2;

    private boolean keepalive = true;

    private int backlog = 128;

    private int readerIdleTimeSeconds = 30;

    private int writerIdleTimeSeconds = 0;

    private int allIdleTimeSeconds = 0;

    private int maxFrameLength = Integer.MAX_VALUE;

    private int lengthFieldOffset = 0;

    private int lengthFieldLength = 4;

    private int lengthAdjustment = 0;

    private int initialBytesToStrip = 4;

    private int channelPoolSize = 100;

    private String scanPackage;

    private int cacheInitialCapacity = 1;

    private long cacheMaximumSize = 250;

    private int cacheConcurrencyLevel = 8;

    private int cacheExpireAfterWrite = 60;

    private String docPath;

    private String serverHost;

    private int serverPort;

    private int clientPingInterval = 10;

    private int clientRetryInterval = 10;

    private int clientBaseSleepTimeMs = 1000;

    private int clientMaxRetries = 30;

    private int clientMaxSleepMs = 29;

    private String callbackPath = "/mynety-callback";

}
