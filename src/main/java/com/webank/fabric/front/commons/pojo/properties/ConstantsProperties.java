package com.webank.fabric.front.commons.pojo.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * properties of constant.
 */
@Data
@Configuration
@ConfigurationProperties("constant")
public class ConstantsProperties {
    private String monitorDisk = "/";
    private boolean monitorEnabled = true;
}
