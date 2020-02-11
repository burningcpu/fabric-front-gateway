package com.webank.fabric.front.commons.pojo.chaincode;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Proposal return parameters.
 */
@Data
@Builder
@Accessors(chain = true)
public class ProposalResponseVO {
    private String chainCodeId;
    private String txId;
    private String peerName;
    private int status;//ChaincodeResponse.Status
    private String message;
}
