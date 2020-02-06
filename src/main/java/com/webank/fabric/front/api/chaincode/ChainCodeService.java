package com.webank.fabric.front.api.chaincode;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;


/**
 * about ChainCode.
 **/
@Slf4j
@Service
public class ChainCodeService {


    /**
     * install ChainCode.
     **/
    public void installChainCode(HFClient hfClient, Collection<Peer> peers, InstallProposalRequest installProposalRequest) throws InvalidArgumentException, ProposalException {
        Collection<ProposalResponse> responses = hfClient.sendInstallProposal(installProposalRequest, peers);
        checkProposalResponse(responses);
    }

    /**
     * instantiate ChainCode.
     **/
    public void instantiateChainCode(Channel channel, Collection<Peer> peers, InstantiateProposalRequest instantiateProposalRequest) throws ChaincodeEndorsementPolicyParseException, IOException, InvalidArgumentException, ProposalException {
        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, peers);
        System.out.print("Sending instantiateProposalRequest to all peers with arguments");
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        System.out.println("ChainCode " + instantiateProposalRequest.getChaincodeName() + " on channel " + channel.getName() + " instantiation " + cf);
        checkProposalResponse(responses);
    }

    /**
     * invoke ChainCode.
     **/
    public void invokeChainCode(Channel channel, TransactionProposalRequest transactionProposalRequest) throws InvalidArgumentException, ProposalException {
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        checkProposalResponse(responses);
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
    }

    /**
     * query ChainCode.
     **/
    public void queryChainCode(Channel channel, QueryByChaincodeRequest queryByChaincodeRequest) throws ProposalException, InvalidArgumentException {
        Collection<ProposalResponse> response = channel.queryByChaincode(queryByChaincodeRequest);
        for (ProposalResponse pres : response) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            System.out.println(stringResponse);
        }
    }

    /**
     * check  proposal response.
     */
    private void checkProposalResponse(Collection<ProposalResponse> responses) {
        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful proposal response Txid:{} from peer:{}", response.getTransactionID(), response.getPeer().getName());
            } else {
                log.error("Failed proposal response Txid:{} from peer:{}", response.getTransactionID(), response.getPeer().getName());
            }
        }
    }

    public String setChainCodeEventListener(Channel channel, String expetedEventName, CountDownLatch latch) throws InvalidArgumentException {
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

    public void upgradeChainCode(Channel channel, Collection<Peer> peers, UpgradeProposalRequest upgradeProposalRequest) throws ProposalException, InvalidArgumentException {
        Collection<ProposalResponse> responses = channel.sendUpgradeProposal(upgradeProposalRequest, peers);
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        System.out.println("ChainCode " + upgradeProposalRequest.getChaincodeName() + " on channel " + channel.getName() + " instantiation " + cf);
        checkProposalResponse(responses);
    }


}
