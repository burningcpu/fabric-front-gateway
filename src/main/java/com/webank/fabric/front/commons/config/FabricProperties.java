package com.webank.fabric.front.commons.config;

import com.webank.fabric.front.commons.pojo.GatewayProperties;
import com.webank.fabric.front.commons.pojo.NetworkProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * properties of fabric.
 */
@Data
@Configuration
@ConfigurationProperties("spring.fabric")
public class FabricProperties {
    private String channel;
    private GatewayProperties gateway;
    private NetworkProperties network;
}
