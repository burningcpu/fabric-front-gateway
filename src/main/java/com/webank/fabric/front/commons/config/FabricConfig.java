package com.webank.fabric.front.commons.config;

import com.webank.fabric.front.commons.pojo.properties.FabricProperties;
import com.webank.fabric.front.commons.pojo.properties.WalletProperties;
import com.webank.fabric.front.commons.utils.GateWayUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * config network.
 */
@Slf4j
@Configuration
public class FabricConfig {
    @Autowired
    private FabricProperties fabricProperties;

    @Bean(name = "localNetwork")
    public Network getNetwork() throws NetworkConfigurationException, IOException, InvalidArgumentException {
        log.debug("Initialize Network start... ");
        NetworkConfig config = NetworkConfig.fromYamlFile(new File(fabricProperties.getNetwork().getFile()));

        if (config == null) {
            throw new RuntimeException("loaded Network config fail. Please check 'spring.fabric.network.file'");
        }

        // Load an existing wallet holding identities used to access the network.
        NetworkConfig.OrgInfo client = config.getClientOrganization();
        WalletProperties walletProperties = fabricProperties.getGateway().getWallet();
        Wallet wallet = GateWayUtils.getWallet(client.getMspId(), walletProperties.getIdentify(), walletProperties.getCertFile(), walletProperties.getPrivateKeyFile());

        // Configure the gateway connection used to access the network.
        Gateway.Builder builder = Gateway.createBuilder()
                .identity(wallet, walletProperties.getIdentify())
                .networkConfig(new FileInputStream(new File(fabricProperties.getNetwork().getFile())));

        // Create a gateway connection
        Gateway gateway = builder.connect();
        Network network = gateway.getNetwork(fabricProperties.getChannel());
        log.debug("Initialize Network success...");
        return network;

    }


}
