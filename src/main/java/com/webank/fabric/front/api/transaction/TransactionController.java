package com.webank.fabric.front.api.transaction;

import com.webank.fabric.front.commons.pojo.base.BaseResponse;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import com.webank.fabric.front.commons.pojo.transaction.ReqTransactionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * controller of transaction.
 */
@Log4j2
@RestController
@Api(value = "/transaction", tags = "about transaction")
@RequestMapping("transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    /**
     * sendTransaction.
     */
    @ApiOperation(value = "send", notes = "send transaction")
    @PostMapping("/send")
    public BaseResponse sendTransaction(@Valid @RequestBody ReqTransactionVO param) {
        String result = transactionService.sendTransaction(param);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, result);
        return baseResponse;
    }
}
