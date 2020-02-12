package com.webank.fabric.front.api.chaincode;

import com.alibaba.fastjson.JSON;
import com.webank.fabric.front.api.sdk.SdkService;
import com.webank.fabric.front.api.transaction.TransactionRequestInitService;
import com.webank.fabric.front.commons.exception.FrontException;
import com.webank.fabric.front.commons.pojo.base.BaseResponse;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import com.webank.fabric.front.commons.pojo.chaincode.ChainCodeInfo;
import com.webank.fabric.front.commons.pojo.chaincode.ProposalResponseVO;
import com.webank.fabric.front.commons.pojo.chaincode.ReqDeployVO;
import com.webank.fabric.front.commons.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * about ChainCode.
 **/
@Slf4j
@Service
public class ChainCodeService {
    @Value("${spring.chaincode.filePath}")
    private String chainCodePath;
    @Autowired
    private SdkService sdkService;
    @Autowired
    private TransactionRequestInitService transactionRequestInitService;
    private static final String CHAIN_CODE_INIT_METHOD_NAME = "init";
    final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * deploy chainCode.
     */
    public String deploy(ReqDeployVO param) {
        String name = param.getChainCodeName();
        String version = param.getVersion();
        String language = param.getChainCodeLang();
        String chainCodeSource = new String(decoder.decode(param.getChainCodeSourceBase64()), StandardCharsets.UTF_8);//
        String fileName = FileUtils.buildChainCodeFileName(param.getChannelName(), name, version);
        String chainCodeDirectory = name + "_" + Instant.now().toEpochMilli();
        //write to file
        writeChainCodeToFile(fileName, language, chainCodeSource, chainCodeDirectory);
        //chainCodeInfo
        ChainCodeInfo chainCodeInfo = new ChainCodeInfo(fileName, version, chainCodePath, chainCodeDirectory, TransactionRequest.Type.GO_LANG, null);

        //peers
        Collection<Peer> peers = sdkService.getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER));
        Collection<Peer> peers1 = peers.stream().filter(peer -> peer.getName().equals("peer0.org1.example.com:7051")).collect(Collectors.toList());
        HFClient hfClient = sdkService.getClient();

        //install
        InstallProposalRequest installProposalRequest = transactionRequestInitService.installChainCodeReqInit(hfClient, chainCodeInfo);
        this.installChainCode(hfClient, peers1, installProposalRequest);

        //instantiate
        InstantiateProposalRequest instantiateProposalRequest = transactionRequestInitService.instantiateChainCodeReqInit(hfClient, chainCodeInfo, CHAIN_CODE_INIT_METHOD_NAME, param.getInitParams());
        this.instantiateChainCode(sdkService.getChannel(), peers1, instantiateProposalRequest);

        return fileName;
    }

    /**
     * write chainCode to file.
     */
    private void writeChainCodeToFile(String fileName, String language, String chainCodeSource, String timeStr) {
        String fileSuffix = FileUtils.chooseChainCodeFileSuffix(language);  //suffix of file
        Path fullPathOfChainCode = Paths.get(chainCodePath, "src", timeStr, fileName + fileSuffix);
        File chainCodeFile = new File(fullPathOfChainCode.toUri());
        FileUtils.writeConstantToFile(chainCodeFile, chainCodeSource);
    }

    /**
     * install ChainCode.
     *
     * @return
     */
    private List<ProposalResponseVO> installChainCode(HFClient hfClient, Collection<Peer> peers, InstallProposalRequest installProposalRequest) {
        Collection<ProposalResponse> responses = null;
        try {
            responses = hfClient.sendInstallProposal(installProposalRequest, peers);
        } catch (ProposalException | InvalidArgumentException ex) {
            throw new FrontException(ConstantCode.INSTALL_CHAIN_CODE_EXCEPTION, ex);
        }
        return parsingProposalResponse(responses);
    }

    /**
     * instantiate ChainCode.
     **/
    private List<ProposalResponseVO> instantiateChainCode(Channel channel, Collection<Peer> peers, InstantiateProposalRequest instantiateProposalRequest) {
        Collection<ProposalResponse> responses;
        try {
            responses = channel.sendInstantiationProposal(instantiateProposalRequest, peers);
        } catch (InvalidArgumentException | ProposalException ex) {
            throw new FrontException(ConstantCode.INSTANTIATE_CHAIN_CODE_EXCEPTION, ex);
        }
        log.debug("Sending instantiateProposalRequest to all peers with arguments");
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        log.info("ChainCode " + instantiateProposalRequest.getChaincodeName() + " on channel " + channel.getName() + " instantiation " + cf);
        return parsingProposalResponse(responses);
    }

    /**
     * invoke ChainCode.
     **/
    private BaseResponse invokeChainCode(Channel channel, TransactionProposalRequest transactionProposalRequest) throws InvalidArgumentException, ProposalException {
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        List<ProposalResponseVO> proposalResponseList = parsingProposalResponse(responses);
        long failCount = proposalResponseList.stream().filter(rsp -> rsp.getStatus() != ChaincodeResponse.Status.SUCCESS.getStatus()).count();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.INVOKE_CHAIN_CODE_EXCEPTION);
        if (failCount > 0) {
            log.error("install proposal request fail,proposalResponseList:{} ", JSON.toJSONString(proposalResponseList));
            baseResponse.setData(proposalResponseList);
            return baseResponse;
        }

        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        return new BaseResponse(ConstantCode.SUCCESS);
    }

    /**
     * query ChainCode.
     **/
    private void queryChainCode(Channel channel, QueryByChaincodeRequest queryByChaincodeRequest) throws ProposalException, InvalidArgumentException {
        Collection<ProposalResponse> response = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse pres : response) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            System.out.println(stringResponse);
        }
    }

    /**
     * Parsing proposal response.
     */
    private List<ProposalResponseVO> parsingProposalResponse(Collection<ProposalResponse> responses) {
        StringBuffer resultMessageBf = new StringBuffer();
        responses.stream().filter(rsp -> rsp.getStatus() != ChaincodeResponse.Status.SUCCESS).forEach(rsp -> resultMessageBf.append(rsp.getMessage()).append(";"));
        if (resultMessageBf.length() > 0) {
            log.error("proposal request fail,resultMessageBf:{} ", resultMessageBf.toString());
            throw new FrontException(ConstantCode.PROPOSAL_REQUEST_EXCEPTION.getCode(), resultMessageBf.toString());
        }

        List<ProposalResponseVO> responseList = new ArrayList<>(responses.size());
        for (ProposalResponse response : responses) {
            ProposalResponseVO rspVO;
            try {
                rspVO = ProposalResponseVO.builder()
                        .chainCodeId(response.getChaincodeID().toString())
                        .status(response.getStatus().getStatus())
                        .txId(response.getTransactionID())
                        .peerName(response.getPeer().getName())
                        .message(response.getMessage())
                        .build();
                responseList.add(rspVO);
            } catch (InvalidArgumentException e) {
                log.error("parsing proposal response exception", e);
                continue;
            }

        }
        return responseList;
    }

    private String setChainCodeEventListener(Channel channel, String expetedEventName, CountDownLatch latch) throws InvalidArgumentException {
        ChaincodeEventListener chaincodeEventListener = (s, blockEvent, chaincodeEvent) -> {
            // TODO: event������
            System.out.println(chaincodeEvent.getEventName());
            System.out.println(new String(chaincodeEvent.getPayload()));
            latch.countDown();
        };
        String eventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
                Pattern.compile(Pattern.quote(expetedEventName)),
                chaincodeEventListener);
        return eventListenerHandle;
    }

    private void upgradeChainCode(Channel channel, Collection<Peer> peers, UpgradeProposalRequest upgradeProposalRequest) throws ProposalException, InvalidArgumentException {
        Collection<ProposalResponse> responses = channel.sendUpgradeProposal(upgradeProposalRequest, peers);
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        System.out.println("ChainCode " + upgradeProposalRequest.getChaincodeName() + " on channel " + channel.getName() + " instantiation " + cf);
        parsingProposalResponse(responses);
    }


}
