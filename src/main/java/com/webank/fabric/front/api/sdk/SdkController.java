package com.webank.fabric.front.api.sdk;

import com.webank.fabric.front.commons.pojo.sdk.PeerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collection;

/**
 * controller of fabric sdk.
 */
@Log4j2
@RestController
@Api(value = "/sdk", tags = "about fabric sdk")
@RequestMapping("sdk")
public class SdkController {
    @Autowired
    private SdkService sdkService;

    /**
     * get peers.
     */
    @ApiOperation(value = "ListPeers", notes = "get peer list")
    @GetMapping("/peers")
    public Collection<PeerVO> ListPeers() {
        return sdkService.getPeerVOs();
    }

    /**
     * get transaction by txId.
     */
    @ApiOperation(value = "getTransactionByTxId", notes = "get transaction info by txId")
    @GetMapping("/transactionInfo/{txId}")
    public byte[] getTransactionByTxId(@PathVariable(value = "txId") String txId) throws InvalidArgumentException, ProposalException {
        return sdkService.getTransactionByTxId(txId);
    }

    /**
     * get blockInfo by txId.
     */
    @ApiOperation(value = "getBlockByTxId", notes = "get block info by txId")
    @GetMapping("/blockInfo/{txId}")
    public byte[] getBlockByTxId(@PathVariable(value = "txId") String txId) throws InvalidArgumentException, ProposalException {
        return sdkService.queryBlockByTransactionId(txId);
    }


    /**
     * get blockInfo by block number.
     */
    @ApiOperation(value = "queryBlockByNumber", notes = "get block by number")
    @GetMapping("/queryBlockByNumber/{blockNumber}")
    public byte[] queryBlockByNumber(@PathVariable(value = "blockNumber") Long blockNumber) throws InvalidArgumentException, ProposalException {
        return sdkService.queryBlockByNumber(blockNumber);
    }

    /**
     * get blockInfo by block hash.
     */
    @ApiOperation(value = "queryBlockByHash", notes = "get block by hash")
    @GetMapping("/queryBlockByHash/{blockHash}")
    public byte[] queryBlockByHash(@PathVariable(value = "blockHash") String blockHash) throws InvalidArgumentException, ProposalException {
        return sdkService.queryBlockByHash(blockHash);
    }

    /**
     * get latest block height of peer.
     */
    @ApiOperation(value = "getPeerBlockNumber", notes = "get latest block height")
    @GetMapping("peerBlockNumber")
    public BigInteger getPeerBlockNumber(@RequestParam(value = "peerUrl") String peerUrl) throws InvalidArgumentException, ProposalException {
        return sdkService.getPeerBlockNumber(peerUrl);
    }

    /**
     * get latest block height of channel.
     */
    @ApiOperation(value = "getChannelBlockNumber", notes = "get latest block height of channel")
    @GetMapping("channelBlockNumber")
    public BigInteger getChannelBlockNumber() throws InvalidArgumentException, ProposalException {
        return sdkService.getChannelBlockNumber();
    }


    /**
     * get current channel.
     */
    @ApiOperation(value = "getChannelName", notes = "get name of channel")
    @GetMapping("channelName")
    public String getChannelName() {
        return sdkService.getChannelName();
    }

    /**
     * get current channel.
     *
     * @return
     */
    @ApiOperation(value = "getChainCodeNameList", notes = "get chainCodeName List")
    @GetMapping("chainCodeNameList")
    public Collection<String> getDiscoveredChainCodeNames() {
        return sdkService.getDiscoveredChainCodeNames();
    }


}
