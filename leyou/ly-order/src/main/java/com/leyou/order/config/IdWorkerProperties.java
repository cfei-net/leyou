package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类用来加载：配置文件的属性
 */
@Data
@ConfigurationProperties("ly.worker")
public class IdWorkerProperties {
    private long workerId;
    private long datacenterId;
}
