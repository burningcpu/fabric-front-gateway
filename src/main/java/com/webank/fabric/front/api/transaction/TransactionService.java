package com.webank.fabric.front.api.transaction;

import com.webank.fabric.front.commons.exception.FrontException;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import com.webank.fabric.front.commons.pojo.transaction.ReqTransactionVO;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * service of transaction.
 */
@Slf4j
@Service
public class TransactionService {
    @Autowired
    private Network network;

    /**
     * send transaction.
     */
    public String sendTransaction(ReqTransactionVO param) {
        Contract contract = network.getContract(param.getChainCodeId());
        try {
            byte[] transactionResult = contract.createTransaction(param.getMethodName())
                    .submit(param.getMethodParams());
            return new String(transactionResult, StandardCharsets.UTF_8);
        } catch (ContractException | TimeoutException | InterruptedException ex) {
            throw new FrontException(ConstantCode.TRANSACTION_EXCEPTION, ex);
        }

    }
}
