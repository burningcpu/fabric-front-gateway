package com.webank.fabric.front.commons.pojo.chaincode;

import lombok.Getter;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.TransactionRequest;

@Getter
public class ChainCodeInfo {
    private final String name;
    private final String version;
    private final String filePath;
    private final String path;
    private final TransactionRequest.Type language;
    private final String chaincodePolicyPath;
    private final ChaincodeID chaincodeID;

    public ChainCodeInfo(String name, String version, String filePath, String path, TransactionRequest.Type language, String chaincodePolicyPath) {
        this.name = name;
        this.version = version;
        this.filePath = filePath;
        this.path = path;
        this.language = language;
        this.chaincodeID = chainCodeInit();
        this.chaincodePolicyPath = chaincodePolicyPath;
    }

    private ChaincodeID chainCodeInit() {
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(this.name)
                .setVersion(this.version);
        if (null != this.path) {
            chaincodeIDBuilder.setPath(this.path);

        }
        return chaincodeIDBuilder.build();
    }
}