package com.webank.fabric.front.commons.pojo.transaction;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * param of transaction request.
 */
@Data
public class ReqTransactionVO {
    @NotEmpty
    private String chainCodeId;
    @NotEmpty
    private String methodName;
    private String[] methodParams = new String[]{};
}
