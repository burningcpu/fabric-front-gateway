package com.webank.fabric.front.commons.pojo.chaincode;

import lombok.Data;
import org.hyperledger.fabric.sdk.TransactionRequest;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * param of deploy chainCode request.
 */
@Data
public class ReqDeployVO {
    @NotEmpty
    private String channelName;
    @NotEmpty
    private String chainCodeName;
    @NotEmpty
    private String chainCodeSource;
    @NotEmpty
    private String version;
    private String chainCodeLang = TransactionRequest.Type.GO_LANG.toString();
    private String[] initParams = new String[]{};


}
