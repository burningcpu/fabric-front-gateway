package com.webank.fabric.front.api.chaincode;

import com.webank.fabric.front.commons.pojo.base.BaseResponse;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import com.webank.fabric.front.commons.pojo.chaincode.ProposalResponseVO;
import com.webank.fabric.front.commons.pojo.chaincode.ReqDeployVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * controller of chainCode.
 */
@Log4j2
@RestController
@Api(value = "/chainCode", tags = "about chainCode")
@RequestMapping("chainCode")
public class ChainCodeController {
    @Autowired
    private ChainCodeService chainCodeService;

    /**
     * deploy chainCode.
     *
     * @return
     */
    @ApiOperation(value = "deploy", notes = "deploy chainCode")
    @GetMapping("/deploy")
    public BaseResponse deployChainCode(@Valid @RequestBody ReqDeployVO param) {
        List<ProposalResponseVO> deployRsp = chainCodeService.deploy(param);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, deployRsp);
        return baseResponse;
    }
}
