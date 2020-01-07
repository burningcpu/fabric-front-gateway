package com.webank.fabric.front.sdk;

import com.webank.fabric.front.sdk.pojo.PeerVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * controller of fabric sdk.
 */
@Log4j2
@RestController
@RequestMapping("sdk")
public class SdkController {
    @Autowired
    private SdkService sdkService;

    /**
     * get peers.
     */
    @ApiOperation(value = "ListPeers", notes = "get peer list")
    @GetMapping("/peers")
    public List<PeerVO> ListPeers() {
        return sdkService.getPeers();
    }

    /**
     * get transaction by txId.
     */
    @ApiOperation(value = "getTransactionByTxId", notes = "get transaction info by txId")
    @GetMapping("/{txId}/transactionInfo")
    public byte[] getTransactionByTxId(@PathVariable(value = "txId") String txId) throws InvalidArgumentException, ProposalException {
        return sdkService.getTransactionByTxId(txId);
    }

    /**
     * get blockInfo by block number.
     */
    @ApiOperation(value = "queryBlockByNumber", notes = "get block bu number")
    @GetMapping("/queryBlockByNumber/{blockNumber}")
    public byte[] queryBlockByNumber(@PathVariable(value = "blockNumber") Long blockNumber) throws InvalidArgumentException, ProposalException {
        return sdkService.queryBlockByNumber(blockNumber);
    }

    /**
     * get blockInfo by block hash.
     */
    @ApiOperation(value = "blockInfo", notes = "get block info")
    @GetMapping("/queryBlockByHash/{blockHash}")
    public byte[] queryBlockByHash(@PathVariable(value = "blockHash") String blockHash) throws InvalidArgumentException, ProposalException {
        return sdkService.queryBlockByHash(blockHash);
    }

    /**
     * get latest block height.
     */
    @ApiOperation(value = "currentBlockHeight", notes = "get latest block height")
    @GetMapping("currentBlockHeight")
    public BigInteger getCurrentBlockHeight(@RequestParam(name = "peerAddress", required = false) String peerAddress) throws InvalidArgumentException, ProposalException {
        return sdkService.getCurrentBlockHeight(peerAddress);
    }

    /**
     * get current channel.
     */
    @ApiOperation(value = "getChannelName", notes = "get name of channel")
    @GetMapping("channelName")
    public String getChannelName() {
        return sdkService.getChannelName();
    }

}