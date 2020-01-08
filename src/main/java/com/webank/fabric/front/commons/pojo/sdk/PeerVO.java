package com.webank.fabric.front.commons.pojo.sdk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hyperledger.fabric.sdk.Peer;

/**
 * peer info of view.
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PeerVO {
    private String peerUrl;
    private String peerName;
    private Integer peerPort;
    private String peerIp;
}
