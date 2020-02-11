package com.webank.fabric.front.api.sdk;

import com.webank.fabric.front.commons.pojo.base.BaseResponse;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
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
     *
     * @return
     */
    @ApiOperation(value = "ListPeers", notes = "get peer list")
    @GetMapping("/peers")
    public BaseResponse ListPeers() {
        Collection<PeerVO> voCollection = sdkService.getPeerVOs();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, voCollection);
        return baseResponse;
    }

    /**
     * get transaction by txId.
     */
    @ApiOperation(value = "getTransactionByTxId", notes = "get transaction info by txId")
    @GetMapping("/transactionInfo/{txId}")
    public BaseResponse getTransactionByTxId(@PathVariable(value = "txId") String txId) throws InvalidArgumentException, ProposalException {
        byte[] trans = sdkService.getTransactionByTxId(txId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, trans);
        return baseResponse;
    }

    /**
     * get blockInfo by txId.
     */
    @ApiOperation(value = "getBlockByTxId", notes = "get block info by txId")
    @GetMapping("/blockInfo/{txId}")
    public BaseResponse getBlockByTxId(@PathVariable(value = "txId") String txId) throws InvalidArgumentException, ProposalException {
        byte[] block = sdkService.queryBlockByTransactionId(txId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, block);
        return baseResponse;
    }


    /**
     * get blockInfo by block number.
     */
    @ApiOperation(value = "queryBlockByNumber", notes = "get block by number")
    @GetMapping("/queryBlockByNumber/{blockNumber}")
    public BaseResponse queryBlockByNumber(@PathVariable(value = "blockNumber") Long blockNumber) throws InvalidArgumentException, ProposalException {
        byte[] block = sdkService.queryBlockByNumber(blockNumber);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, block);
        return baseResponse;
    }

    /**
     * get blockInfo by block hash.
     */
    @ApiOperation(value = "queryBlockByHash", notes = "get block by hash")
    @GetMapping("/queryBlockByHash/{blockHash}")
    public BaseResponse queryBlockByHash(@PathVariable(value = "blockHash") String blockHash) throws InvalidArgumentException, ProposalException {
        byte[] block = sdkService.queryBlockByHash(blockHash);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, block);
        return baseResponse;
    }

    /**
     * get latest block height of peer.
     */
    @ApiOperation(value = "getPeerBlockNumber", notes = "get latest block height")
    @GetMapping("peerBlockNumber")
    public BaseResponse getPeerBlockNumber(@RequestParam(value = "peerUrl") String peerUrl) throws InvalidArgumentException, ProposalException {
        BigInteger blockNumber = sdkService.getPeerBlockNumber(peerUrl);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, blockNumber);
        return baseResponse;
    }

    /**
     * get latest block height of channel.
     */
    @ApiOperation(value = "getChannelBlockNumber", notes = "get latest block height of channel")
    @GetMapping("channelBlockNumber")
    public BaseResponse getChannelBlockNumber() throws InvalidArgumentException, ProposalException {
        BigInteger blockNumber = sdkService.getChannelBlockNumber();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, blockNumber);
        return baseResponse;
    }


    /**
     * get current channel.
     */
    @ApiOperation(value = "getChannelName", notes = "get name of channel")
    @GetMapping("channelName")
    public BaseResponse getChannelName() {
        String channelName = sdkService.getChannelName();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, channelName);
        return baseResponse;
    }

    /**
     * get current channel.
     *
     * @return
     */
    @ApiOperation(value = "getChainCodeNameList", notes = "get chainCodeName List")
    @GetMapping("chainCodeNameList")
    public BaseResponse getDiscoveredChainCodeNames() {
        Collection<String> chainCodeNames = sdkService.getDiscoveredChainCodeNames();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, chainCodeNames);
        return baseResponse;
    }


}
