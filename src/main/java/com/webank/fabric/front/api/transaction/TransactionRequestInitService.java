package com.webank.fabric.front.api.transaction;

import com.webank.fabric.front.commons.exception.FrontException;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import com.webank.fabric.front.commons.pojo.chaincode.ChainCodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * methods for get object of proposalRequest.
 */
@Slf4j
@Service
public class TransactionRequestInitService {

    /**
     * method for get InstallProposalRequest.
     */
    public InstallProposalRequest installChainCodeReqInit(HFClient hfClient, ChainCodeInfo chaincodeInfo) {
        InstallProposalRequest installProposalRequest = hfClient.newInstallProposalRequest();
        ChaincodeID chaincodeID = chaincodeInfo.getChaincodeID();
        installProposalRequest.setChaincodeID(chaincodeID);
        try {
            installProposalRequest.setChaincodeSourceLocation(Paths.get(chaincodeInfo.getFilePath()).toFile());
        } catch (InvalidArgumentException ex) {
            log.error("installChainCodeReqInit fail", ex);
            throw new FrontException(ConstantCode.OPERATION_EXCEPTION, ex);
        }
        installProposalRequest.setChaincodeVersion(chaincodeID.getVersion());
        // installProposalRequest.setChaincodePath(chaincodeInfo.getPath());
        installProposalRequest.setChaincodeLanguage(chaincodeInfo.getLanguage());

        return installProposalRequest;
    }

    /**
     * method for get InstantiateProposalRequest.
     */
    public InstantiateProposalRequest instantiateChainCodeReqInit(HFClient hfClient, ChainCodeInfo chaincodeInfo, String functionName, String[] argv) {
        InstantiateProposalRequest instantiateProposalRequest = hfClient.newInstantiationProposalRequest();
        ChaincodeID chaincodeID = chaincodeInfo.getChaincodeID();
        instantiateProposalRequest.setProposalWaitTime(180000);
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setChaincodeLanguage(chaincodeInfo.getLanguage());
        instantiateProposalRequest.setFcn(functionName);
        instantiateProposalRequest.setArgs(argv);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        try {
            instantiateProposalRequest.setTransientMap(tm);

            String policyPath = chaincodeInfo.getChaincodePolicyPath();
            if (policyPath != null) {
                ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
                chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
                instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
            }
        } catch (InvalidArgumentException | IOException | ChaincodeEndorsementPolicyParseException ex) {
            log.error("instantiateChainCodeReqInit fail", ex);
            throw new FrontException(ConstantCode.OPERATION_EXCEPTION, ex);
        }
        return instantiateProposalRequest;
    }

    /**
     * method for get UpgradeProposalRequest.
     */
    public UpgradeProposalRequest upgradeChainCodeReqInit(HFClient hfClient, ChainCodeInfo chaincodeInfo, String functionName, String[] argv) {
        UpgradeProposalRequest proposalRequest = hfClient.newUpgradeProposalRequest();
        ChaincodeID chaincodeID = chaincodeInfo.getChaincodeID();
        proposalRequest.setProposalWaitTime(180000);
        proposalRequest.setChaincodeID(chaincodeID);
        proposalRequest.setChaincodeLanguage(chaincodeInfo.getLanguage());
        proposalRequest.setFcn(functionName);
        proposalRequest.setArgs(argv);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        try {
            proposalRequest.setTransientMap(tm);

            String policyPath = chaincodeInfo.getChaincodePolicyPath();
            if (policyPath != null) {
                ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
                chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
                proposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
            }
        } catch (InvalidArgumentException | IOException | ChaincodeEndorsementPolicyParseException ex) {
            log.error("upgradeChainCodeReqInit fail", ex);
            throw new FrontException(ConstantCode.OPERATION_EXCEPTION, ex);
        }
        return proposalRequest;
    }

    /**
     * method for get invokeChainCodeReqInit.
     */
    public TransactionProposalRequest invokeChainCodeReqInit(HFClient hfClient,
                                                             ChainCodeInfo chaincodeInfo, String functionName, String[] argv) {
        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        ChaincodeID chaincodeID = chaincodeInfo.getChaincodeID();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(chaincodeInfo.getLanguage());
        transactionProposalRequest.setFcn(functionName);
        transactionProposalRequest.setArgs(argv);
        transactionProposalRequest.setProposalWaitTime(1000);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); //Just some extra junk in transient map
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
        tm2.put("result", ":)".getBytes(UTF_8));  // This should be returned in the payload see chaincode why.
        try {
            transactionProposalRequest.setTransientMap(tm2);
        } catch (InvalidArgumentException ex) {
            log.error("invokeChainCodeReqInit fail", ex);
        }
        return transactionProposalRequest;
    }

    /**
     * method for get queryByChainCodeRequest.
     */
    public QueryByChaincodeRequest queryChainCodeReqInit(HFClient hfClient, ChaincodeID chaincodeID,
                                                         String functionName, String[] argv) {
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        queryByChaincodeRequest.setChaincodeID(chaincodeID);
        queryByChaincodeRequest.setFcn(functionName);
        queryByChaincodeRequest.setArgs(argv);
        return queryByChaincodeRequest;
    }
}
