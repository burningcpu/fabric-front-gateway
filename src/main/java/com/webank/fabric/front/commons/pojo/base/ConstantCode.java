/**
 * Copyright 2014-2019  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.fabric.front.commons.pojo.base;

/**
 * A-BB-CCC A:error level. <br/>
 * 1:system exception <br/>
 * 2:business exception <br/>
 * B:project number <br/>
 * fabric-front:05 <br/>
 * C: error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* system exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(105000, "system exception");

    /**
     * Business exception.
     */
    public static final RetCode OPERATION_EXCEPTION = RetCode.mark(205000, "operation exception");
    public static final RetCode INSTALL_CHAIN_CODE_EXCEPTION = RetCode.mark(205001, "install chainCode exception");
    public static final RetCode INVOKE_CHAIN_CODE_EXCEPTION = RetCode.mark(205002, "invoke chainCode failed");
    public static final RetCode INSTANTIATE_CHAIN_CODE_EXCEPTION = RetCode.mark(205003, "instantiate chainCode exception");
    public static final RetCode PROPOSAL_REQUEST_EXCEPTION = RetCode.mark(205004, "proposal request exception");
    public static final RetCode TRANSACTION_EXCEPTION = RetCode.mark(205005, "send transaction exception");


    /* param exception */
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(305000, "param exception");

}
