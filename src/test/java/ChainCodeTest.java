import com.alibaba.fastjson.JSON;
import com.webank.fabric.front.Application;
import com.webank.fabric.front.api.chaincode.ChainCodeService;
import com.webank.fabric.front.api.transaction.TransactionRequestInitService;
import com.webank.fabric.front.commons.pojo.base.BaseResponse;
import com.webank.fabric.front.commons.pojo.chaincode.ProposalResponseVO;
import com.webank.fabric.front.commons.pojo.chaincode.ReqDeployVO;
import com.webank.fabric.front.commons.utils.FrontUtils;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ChainCodeTest {
    @Autowired
    private ChainCodeService chainCodeService;
    @Autowired
    private TransactionRequestInitService transactionRequestInitService;
    private static Channel channel;
    private static Network network;
    private static GatewayImpl gateway;

    @Qualifier(value = "localNetwork")
    @Autowired
    public void setChannel(Network network) {
        Objects.requireNonNull(network, "init SdkService fail. network is null");
        this.network = network;
        this.channel = network.getChannel();
        gateway = (GatewayImpl) network.getGateway();
    }

//    @Test
//    public void chainCodeDeploy() throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException {
//        //get chainCodeInfo
//        ChainCodeInfo chainCodeInfo = getChainCodeInfo();
//        //install
//        chainCodeInstall(chainCodeInfo);
//        //instantiate
//        chainCodeInstantiate(chainCodeInfo);
//
//    }
//
//    private ChainCodeInfo getChainCodeInfo() {
//        String name = "myhei";
//        String version = "v1.0";
//        //chaincodeSource is mostly likely the users GOPATH
//        String filePath = "E:\\codes\\fabric\\fabric-front-gateway\\mycc";
//        String path = "hello";
//        TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;
//        String policyPath = null;
//        ChainCodeInfo chainCodeInfo = new ChainCodeInfo(name, version, filePath, path, language, policyPath);
//        return chainCodeInfo;
//    }
//
//    public void chainCodeInstall(ChainCodeInfo chainCodeInfo) throws InvalidArgumentException, ProposalException {
//        Collection<Peer> peers = channel.getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER));
//        Collection<Peer> peers1 = peers.stream().filter(peer -> peer.getName().equals("peer0.org1.example.com:7051")).collect(Collectors.toList());
//        HFClient hfClient = gateway.getClient();
//        InstallProposalRequest installProposalRequest = transactionRequestInitService.installChainCodeReqInit(hfClient, chainCodeInfo);
//        chainCodeService.installChainCode(hfClient, peers1, installProposalRequest);
//    }
//
//    public void chainCodeInstantiate(ChainCodeInfo chainCodeInfo) throws InvalidArgumentException, ProposalException, IOException, ChaincodeEndorsementPolicyParseException {
//        Collection<Peer> peers = channel.getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER));
//        Collection<Peer> peers1 = peers.stream().filter(peer -> peer.getName().equals("peer0.org1.example.com:7051")).collect(Collectors.toList());
//
//        HFClient hfClient = gateway.getClient();
//        InstantiateProposalRequest instantiateProposalRequest = transactionRequestInitService.instantiateChainCodeReqInit(hfClient, chainCodeInfo, "init", new String[]{"a", "100", "b", "200"});
//        chainCodeService.instantiateChainCode(channel, peers1, instantiateProposalRequest);
//    }


    @Test
    public void deployTest() {


        //param
        ReqDeployVO reqDeployVO = new ReqDeployVO();
        reqDeployVO.setChainCodeLang(TransactionRequest.Type.GO_LANG.toString());
        reqDeployVO.setChainCodeName("myhello");
        reqDeployVO.setChannelName("mychannel");
        reqDeployVO.setVersion("1_8");
        reqDeployVO.setInitParams(new String[]{"a", "100", "b", "200"});
        reqDeployVO.setChainCodeSource(readChainCode());
        String baseResponse = chainCodeService.deploy(reqDeployVO);
        System.out.println(format("deploy baseResponse:%s", JSON.toJSONString(baseResponse)));
    }

    @Test
    public void transactionTest() throws IOException, InterruptedException, TimeoutException, ContractException {
        // Obtain a smart contract deployed on the network.
        Contract contract = network.getContract("myhello::chaincode::v1.0");//.getContract("mycc");

        // Evaluate transactions that query state from the ledger.
        byte[] queryResult = contract.createTransaction("query")
                .submit("a");
        String resultq = new String(queryResult, StandardCharsets.UTF_8);
        System.out.println("+++++++++++++++++++++++++++++query result:" + new String(queryResult, StandardCharsets.UTF_8));

        // Submit transactions that store state to the ledger.
        byte[] invokeResult = contract.createTransaction("invoke")
                .submit("a", "b", "10");
        String resulti = new String(invokeResult, StandardCharsets.UTF_8);
        System.out.println("+++++++++++++++++++++++++++++send result:" + new String(invokeResult, StandardCharsets.UTF_8));

    }


    private static String readChainCode() {
        StringBuffer chainCodeSource = new StringBuffer();
        Path path = Paths.get("E:\\codes\\fabric\\fabric-front-gateway\\mycc\\src\\hello\\myhei.go");
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(str -> chainCodeSource.append(str).append("\r\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source = chainCodeSource.toString();
        System.out.println(source);
        return source;
    }
}
