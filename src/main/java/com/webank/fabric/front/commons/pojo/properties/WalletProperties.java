package com.webank.fabric.front.commons.pojo.properties;

import lombok.Data;

@Data
public class WalletProperties {
    private String identify;
    private String certFile;
    private String privateKeyFile;
}
