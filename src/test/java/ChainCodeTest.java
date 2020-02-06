import com.webank.fabric.front.Application;
import com.webank.fabric.front.api.chaincode.ChainCodeService;
import com.webank.fabric.front.api.transaction.TransactionRequestInitService;
import com.webank.fabric.front.commons.pojo.chaincode.ChainCodeInfo;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

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

    @Test
    public void chainCodeDeploy() throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException {
        //get chainCodeInfo
        ChainCodeInfo chainCodeInfo = getChainCodeInfo();
        //install
        chainCodeInstall(chainCodeInfo);
        //instantiate
        chainCodeInstantiate(chainCodeInfo);

    }

    private ChainCodeInfo getChainCodeInfo() {
        String name = "myhello";
        String version = "v1.0";
        //chaincodeSource is mostly likely the users GOPATH
        String filePath = "E:\\codes\\fabric\\fabric-front-gateway\\mycc";
        String path = "chaincode";
        TransactionRequest.Type language = TransactionRequest.Type.GO_LANG;
        String policyPath = null;
        ChainCodeInfo chainCodeInfo = new ChainCodeInfo(name, version, filePath, path, language, policyPath);
        return chainCodeInfo;
    }

    public void chainCodeInstall(ChainCodeInfo chainCodeInfo) throws InvalidArgumentException, ProposalException {
        Collection<Peer> peers = channel.getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER));
        HFClient hfClient = gateway.getClient();
        InstallProposalRequest installProposalRequest = transactionRequestInitService.installChainCodeReqInit(hfClient, chainCodeInfo);
        chainCodeService.installChainCode(hfClient, peers, installProposalRequest);
    }

    public void chainCodeInstantiate(ChainCodeInfo chainCodeInfo) throws InvalidArgumentException, ProposalException, IOException, ChaincodeEndorsementPolicyParseException {
        Collection<Peer> peers = channel.getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER));
        HFClient hfClient = gateway.getClient();
        InstantiateProposalRequest instantiateProposalRequest = transactionRequestInitService.instantiateChainCodeReqInit(hfClient, chainCodeInfo, "init", new String[]{"a", "100", "b", "200"});
        chainCodeService.instantiateChainCode(channel, peers, instantiateProposalRequest);
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


//    private static byte[] readChainCode(){
//        String filePath = "E:\\codes\\fabric\\fabric-front-gateway\\mycc\\chaincode_example02.go";
//        File chaiCodeFile = new File(filePath);
//
//    }
}
