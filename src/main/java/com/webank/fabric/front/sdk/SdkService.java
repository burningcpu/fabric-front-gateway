package com.webank.fabric.front.sdk;

import com.webank.fabric.front.commons.pojo.sdk.PeerVO;
import com.webank.fabric.front.commons.utils.FrontUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

/**
 * service of fabric sdk.
 */
@Slf4j
@Service
public class SdkService {
    private static Channel channel;

    @Qualifier(value = "localNetwork")
    @Autowired
    public void setChannel(Network network) {
        Objects.requireNonNull(network, "init SdkService fail. network is null");
        this.channel = network.getChannel();
    }

    /**
     * get peers.
     */
    public List<PeerVO> getPeers() {
        List<PeerVO> peers = Lists.newArrayList();
        //query peers
        Optional<Collection<Peer>> peersOptional = Optional.ofNullable(channel.getPeers());
        if (peersOptional.isPresent()) {
            //foreach peers
            peersOptional.get().stream().forEach(p -> peers.add(createPeerVO(p)));
        }
        return peers;
    }

    /**
     * create object of PeerVO by Peer.
     */
    private PeerVO createPeerVO(Peer peer) {
        String[] protocolDomainPort = peer.getUrl().split(":");
        String domain = protocolDomainPort[1].substring(2);
        PeerVO peerVO = PeerVO.builder()
                .peerUrl(peer.getUrl())
                .peerIp(FrontUtils.isIP(domain) ? domain : null)
                .peerPort(Integer.valueOf(protocolDomainPort[2]))
                .peerName(peer.getName().split(":")[0])
                .build();
        return peerVO;
    }


    /**
     * get transaction by txId.
     */
    public byte[] getTransactionByTxId(String txId) throws InvalidArgumentException, ProposalException {
        Optional<TransactionInfo> transactionInfoOptional = Optional.ofNullable(channel.queryTransactionByID(txId));
        if (!transactionInfoOptional.isPresent())
            return null;
        return transactionInfoOptional.map(trans -> trans.getProcessedTransaction().toByteArray()).get();
    }


    /**
     * get blockInfo by block height.
     */
    public byte[] queryBlockByNumber(Long blockNumber) throws ProposalException, InvalidArgumentException {
        Optional<BlockInfo> blockInfoOptional = Optional.ofNullable(channel.queryBlockByNumber(blockNumber));
        if (!blockInfoOptional.isPresent())
            return null;

        return blockInfoOptional.map(blockInfo -> blockInfo.getBlock()).map(block -> block.toByteArray()).get();

    }

    /**
     * get blockInfo by block height.
     */
    public byte[] queryBlockByHash(String blockHash) throws ProposalException, InvalidArgumentException {
        Optional<BlockInfo> blockInfoOptional = Optional.ofNullable(channel.queryBlockByHash(blockHash.getBytes()));
        if (!blockInfoOptional.isPresent())
            return null;

        return blockInfoOptional.map(blockInfo -> blockInfo.getBlock()).map(block -> block.toByteArray()).get();

    }


    /**
     * get latest block height of peer.
     */
    public BigInteger getPeerBlockNumber(String peerUrl) throws ProposalException, InvalidArgumentException {
        Optional<Collection<Peer>> peersOptional = Optional.ofNullable(channel.getPeers());
        if (!peersOptional.isPresent())
            return null;

        Peer peer = peersOptional.get().stream().filter(p -> peerUrl.equals(p.getUrl())).findFirst().get();
        return getPeerBlockHeight(peer);
    }

    /**
     * get latest block number of channel.
     */
    public BigInteger getChannelBlockNumber() throws ProposalException, InvalidArgumentException {
        Optional<BlockchainInfo> blockChainInfoOptional = Optional.ofNullable(channel.queryBlockchainInfo());
        if (!blockChainInfoOptional.isPresent())
            return null;

        return BigInteger.valueOf(blockChainInfoOptional.map(chainInfo -> chainInfo.getHeight()).get());

    }

    /**
     * get latest block height of peer.
     */
    public BigInteger getPeerBlockHeight(Peer peer) throws ProposalException, InvalidArgumentException {
        Optional<BlockchainInfo> blockChainInfoOptional = Optional.ofNullable(channel.queryBlockchainInfo(peer));
        if (!blockChainInfoOptional.isPresent())
            return null;

        return BigInteger.valueOf(blockChainInfoOptional.map(chainInfo -> chainInfo.getHeight()).get());

    }

    /**
     * get name of current channel.
     */
    public String getChannelName() {
        return channel.getName();
    }
}
