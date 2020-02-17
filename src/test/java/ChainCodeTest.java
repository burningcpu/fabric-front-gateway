import com.alibaba.fastjson.JSON;
import com.webank.fabric.front.Application;
import com.webank.fabric.front.api.chaincode.ChainCodeService;
import com.webank.fabric.front.api.transaction.TransactionRequestInitService;
import com.webank.fabric.front.commons.pojo.chaincode.ReqDeployVO;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TransactionRequest;
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

//        {
//            "chainCodeLang": "GO_LANG",
//                "chainCodeName": "hh",
//                "chainCodeSourceBase64": "cGFja2FnZSBtYWluCgppbXBvcnQgKAogICAgImZtdCIKICAgICJzdHJjb252IgogICAgImdpdGh1Yi5jb20vaHlwZXJsZWRnZXIvZmFicmljL2NvcmUvY2hhaW5jb2RlL3NoaW0iCiAgICBwYiAiZ2l0aHViLmNvbS9oeXBlcmxlZGdlci9mYWJyaWMvcHJvdG9zL3BlZXIiCikKCnR5cGUgTEdTaW1wbGVDaGFpbmNvZGUgc3RydWN0IHsKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIEluaXQoc3R1YiBzaGltLkNoYWluY29kZVN0dWJJbnRlcmZhY2UpIHBiLlJlc3BvbnNlIHsKICAgIGZtdC5QcmludGxuKCJMR1NpbXBsZUNoYWluY29kZSAtPiBJbml0IikKICAgIF8sIGFyZ3MgOj0gc3R1Yi5HZXRGdW5jdGlvbkFuZFBhcmFtZXRlcnMoKQogICAgdmFyIEEsIEIgc3RyaW5nCiAgICB2YXIgQXZhbCwgQnZhbCBpbnQKICAgIHZhciBlcnIgZXJyb3IKCiAgICBpZiBsZW4oYXJncykgIT0gNCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOaVsOmHj+S4jeWvuSIpCiAgICB9CgogICAgQSA9IGFyZ3NbMF0KICAgIEF2YWwsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzFdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOWAvOexu+Wei+S4jeWvuSIpCiAgICB9CgogICAgQiA9IGFyZ3NbMl0KICAgIEJ2YWwsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzNdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOWAvOexu+Wei+S4jeWvuSIpCiAgICB9CgogICAgZXJyID0gc3R1Yi5QdXRTdGF0ZShBLCBbXWJ5dGUoc3RyY29udi5JdG9hKEF2YWwpKSkKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKGVyci5FcnJvcigpKQogICAgfQoKICAgIGVyciA9IHN0dWIuUHV0U3RhdGUoQiwgW11ieXRlKHN0cmNvbnYuSXRvYShCdmFsKSkpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcihlcnIuRXJyb3IoKSkKICAgIH0KCiAgICByZXR1cm4gc2hpbS5TdWNjZXNzKG5pbCkKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIEludm9rZShzdHViIHNoaW0uQ2hhaW5jb2RlU3R1YkludGVyZmFjZSkgcGIuUmVzcG9uc2UgewogICAgZm10LlByaW50bG4oIkxHU2ltcGxlQ2hhaW5jb2RlIC0+IEludm9rZSIpCgogICAgZnVuY3Rpb24sIGFyZ3MgOj0gc3R1Yi5HZXRGdW5jdGlvbkFuZFBhcmFtZXRlcnMoKQogICAgaWYgZnVuY3Rpb24gPT0gImludm9rZSIgewoKICAgICAgICByZXR1cm4gdC5pbnZva2Uoc3R1YiwgYXJncykKICAgIH0gZWxzZSBpZiBmdW5jdGlvbiA9PSAiZGVsZXRlIiB7CgogICAgICAgIHJldHVybiB0LmRlbGV0ZShzdHViLCBhcmdzKQogICAgfSBlbHNlIGlmIGZ1bmN0aW9uID09ICJxdWVyeSIgewoKICAgICAgICByZXR1cm4gdC5xdWVyeShzdHViLCBhcmdzKQogICAgfQoKICAgIHJldHVybiBzaGltLkVycm9yKCLmlrnms5XlkI3kuI3lr7nvvIzlj6rmlK/mjIEgaW52b2tl77yMZGVsZXRl77yMcXVlcnkiKQp9CgovL+S6pOaYk+i9rOi0pgpmdW5jICh0ICpMR1NpbXBsZUNoYWluY29kZSkgaW52b2tlKHN0dWIgc2hpbS5DaGFpbmNvZGVTdHViSW50ZXJmYWNlLCBhcmdzIFtdc3RyaW5nKSBwYi5SZXNwb25zZSB7CiAgICB2YXIgQSwgQiBzdHJpbmcKICAgIHZhciBBdmFsLCBCdmFsIGludAogICAgdmFyIFggaW50CiAgICB2YXIgZXJyIGVycm9yCgogICAgaWYgbGVuKGFyZ3MpICE9IDMgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLlj4LmlbDmlbDph4/kuI3lr7kiKQogICAgfQoKICAgIEEgPSBhcmdzWzBdCiAgICBCID0gYXJnc1sxXQoKICAgIEF2YWxieXRlcywgZXJyIDo9IHN0dWIuR2V0U3RhdGUoQSkKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLmsqHmib7liLDnrKzkuIDkuKrnmoTnlKjmiLfph5Hpop0iKQogICAgfQogICAgaWYgQXZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuesrOS4gOS4queUqOaIt+mHkemineS4um5pbCIpCiAgICB9CiAgICBBdmFsLCBfID0gc3RyY29udi5BdG9pKHN0cmluZyhBdmFsYnl0ZXMpKQoKICAgIEJ2YWxieXRlcywgZXJyIDo9IHN0dWIuR2V0U3RhdGUoQikKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLmsqHmib7liLDnrKzkuozkuKrnmoTnlKjmiLfph5Hpop0iKQogICAgfQogICAgaWYgQnZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuesrOS6jOS4queUqOaIt+mHkemineS4um5pbCIpCiAgICB9CiAgICBCdmFsLCBfID0gc3RyY29udi5BdG9pKHN0cmluZyhCdmFsYnl0ZXMpKQoKICAgIFgsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzJdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIui9rOi0pumHkemineW8guW4uCIpCiAgICB9CiAgICBBdmFsID0gQXZhbCAtIFgKICAgIEJ2YWwgPSBCdmFsICsgWAogICAgZm10LlByaW50ZigiQXZhbCA9ICVkLCBCdmFsID0gJWRcbiIsIEF2YWwsIEJ2YWwpCgogICAgLy/ph43mlrDmm7TmlrDnlKjmiLfph5Hpop0KICAgIGVyciA9IHN0dWIuUHV0U3RhdGUoQSwgW11ieXRlKHN0cmNvbnYuSXRvYShBdmFsKSkpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcihlcnIuRXJyb3IoKSkKICAgIH0KCiAgICBlcnIgPSBzdHViLlB1dFN0YXRlKEIsIFtdYnl0ZShzdHJjb252Lkl0b2EoQnZhbCkpKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoZXJyLkVycm9yKCkpCiAgICB9CgogICAgcmV0dXJuIHNoaW0uU3VjY2VzcyhuaWwpCn0KCmZ1bmMgKHQgKkxHU2ltcGxlQ2hhaW5jb2RlKSBkZWxldGUoc3R1YiBzaGltLkNoYWluY29kZVN0dWJJbnRlcmZhY2UsIGFyZ3MgW11zdHJpbmcpIHBiLlJlc3BvbnNlIHsKICAgIGlmIGxlbihhcmdzKSAhPSAxIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcigi5Y+C5pWw5byC5bi4IikKICAgIH0KICAgIEEgOj0gYXJnc1swXQoKICAgIGVyciA6PSBzdHViLkRlbFN0YXRlKEEpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcigi5Yig6Zmk55So5oi35byC5bi4IikKICAgIH0KCiAgICByZXR1cm4gc2hpbS5TdWNjZXNzKG5pbCkKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIHF1ZXJ5KHN0dWIgc2hpbS5DaGFpbmNvZGVTdHViSW50ZXJmYWNlLCBhcmdzIFtdc3RyaW5nKSBwYi5SZXNwb25zZSB7CiAgICB2YXIgQSBzdHJpbmcKICAgIHZhciBlcnIgZXJyb3IKCiAgICBpZiBsZW4oYXJncykgIT0gMSB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuafpeivouWPguaVsOW8guW4uCIpCiAgICB9CgogICAgQSA9IGFyZ3NbMF0KCiAgICBBdmFsYnl0ZXMsIGVyciA6PSBzdHViLkdldFN0YXRlKEEpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICBqc29uUmVzcCA6PSAie1wiRXJyb3JcIjpcIueUqOaIt+mHkemineafpeivouW8guW4uCAiICsgQSArICJcIn0iCiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoanNvblJlc3ApCiAgICB9CgogICAgaWYgQXZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAganNvblJlc3AgOj0gIntcIkVycm9yXCI6XCLnlKjmiLfph5Hpop3kuLpuaWwgIiArIEEgKyAiXCJ9IgogICAgICAgIHJldHVybiBzaGltLkVycm9yKGpzb25SZXNwKQogICAgfQoKICAgIGpzb25SZXNwIDo9ICJ7XCJOYW1lXCI6XCIiICsgQSArICJcIixcIkFtb3VudFwiOlwiIiArIHN0cmluZyhBdmFsYnl0ZXMpICsgIlwifSIKICAgIGZtdC5QcmludGYoIlF1ZXJ5IFJlc3BvbnNlOiVzXG4iLCBqc29uUmVzcCkKICAgIHJldHVybiBzaGltLlN1Y2Nlc3MoQXZhbGJ5dGVzKQp9CgpmdW5jIG1haW4oKSB7CiAgICBlcnIgOj0gc2hpbS5TdGFydChuZXcoTEdTaW1wbGVDaGFpbmNvZGUpKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgZm10LlByaW50Zigi6ZO+56CB6YOo572y5aSx6LSlOiAlcyIsIGVycikKICAgIH0KfQo=",
//                "channelName": "mychannel",
//                "initParams": ["a", "200", "b", "500"],
//            "version": "1"
//        }

        String sourceBase64 = "cGFja2FnZSBtYWluCgppbXBvcnQgKAogICAgImZtdCIKICAgICJzdHJjb252IgogICAgImdpdGh1Yi5jb20vaHlwZXJsZWRnZXIvZmFicmljL2NvcmUvY2hhaW5jb2RlL3NoaW0iCiAgICBwYiAiZ2l0aHViLmNvbS9oeXBlcmxlZGdlci9mYWJyaWMvcHJvdG9zL3BlZXIiCikKCnR5cGUgTEdTaW1wbGVDaGFpbmNvZGUgc3RydWN0IHsKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIEluaXQoc3R1YiBzaGltLkNoYWluY29kZVN0dWJJbnRlcmZhY2UpIHBiLlJlc3BvbnNlIHsKICAgIGZtdC5QcmludGxuKCJMR1NpbXBsZUNoYWluY29kZSAtPiBJbml0IikKICAgIF8sIGFyZ3MgOj0gc3R1Yi5HZXRGdW5jdGlvbkFuZFBhcmFtZXRlcnMoKQogICAgdmFyIEEsIEIgc3RyaW5nCiAgICB2YXIgQXZhbCwgQnZhbCBpbnQKICAgIHZhciBlcnIgZXJyb3IKCiAgICBpZiBsZW4oYXJncykgIT0gNCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOaVsOmHj+S4jeWvuSIpCiAgICB9CgogICAgQSA9IGFyZ3NbMF0KICAgIEF2YWwsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzFdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOWAvOexu+Wei+S4jeWvuSIpCiAgICB9CgogICAgQiA9IGFyZ3NbMl0KICAgIEJ2YWwsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzNdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuWPguaVsOWAvOexu+Wei+S4jeWvuSIpCiAgICB9CgogICAgZXJyID0gc3R1Yi5QdXRTdGF0ZShBLCBbXWJ5dGUoc3RyY29udi5JdG9hKEF2YWwpKSkKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKGVyci5FcnJvcigpKQogICAgfQoKICAgIGVyciA9IHN0dWIuUHV0U3RhdGUoQiwgW11ieXRlKHN0cmNvbnYuSXRvYShCdmFsKSkpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcihlcnIuRXJyb3IoKSkKICAgIH0KCiAgICByZXR1cm4gc2hpbS5TdWNjZXNzKG5pbCkKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIEludm9rZShzdHViIHNoaW0uQ2hhaW5jb2RlU3R1YkludGVyZmFjZSkgcGIuUmVzcG9uc2UgewogICAgZm10LlByaW50bG4oIkxHU2ltcGxlQ2hhaW5jb2RlIC0+IEludm9rZSIpCgogICAgZnVuY3Rpb24sIGFyZ3MgOj0gc3R1Yi5HZXRGdW5jdGlvbkFuZFBhcmFtZXRlcnMoKQogICAgaWYgZnVuY3Rpb24gPT0gImludm9rZSIgewoKICAgICAgICByZXR1cm4gdC5pbnZva2Uoc3R1YiwgYXJncykKICAgIH0gZWxzZSBpZiBmdW5jdGlvbiA9PSAiZGVsZXRlIiB7CgogICAgICAgIHJldHVybiB0LmRlbGV0ZShzdHViLCBhcmdzKQogICAgfSBlbHNlIGlmIGZ1bmN0aW9uID09ICJxdWVyeSIgewoKICAgICAgICByZXR1cm4gdC5xdWVyeShzdHViLCBhcmdzKQogICAgfQoKICAgIHJldHVybiBzaGltLkVycm9yKCLmlrnms5XlkI3kuI3lr7nvvIzlj6rmlK/mjIEgaW52b2tl77yMZGVsZXRl77yMcXVlcnkiKQp9CgovL+S6pOaYk+i9rOi0pgpmdW5jICh0ICpMR1NpbXBsZUNoYWluY29kZSkgaW52b2tlKHN0dWIgc2hpbS5DaGFpbmNvZGVTdHViSW50ZXJmYWNlLCBhcmdzIFtdc3RyaW5nKSBwYi5SZXNwb25zZSB7CiAgICB2YXIgQSwgQiBzdHJpbmcKICAgIHZhciBBdmFsLCBCdmFsIGludAogICAgdmFyIFggaW50CiAgICB2YXIgZXJyIGVycm9yCgogICAgaWYgbGVuKGFyZ3MpICE9IDMgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLlj4LmlbDmlbDph4/kuI3lr7kiKQogICAgfQoKICAgIEEgPSBhcmdzWzBdCiAgICBCID0gYXJnc1sxXQoKICAgIEF2YWxieXRlcywgZXJyIDo9IHN0dWIuR2V0U3RhdGUoQSkKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLmsqHmib7liLDnrKzkuIDkuKrnmoTnlKjmiLfph5Hpop0iKQogICAgfQogICAgaWYgQXZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuesrOS4gOS4queUqOaIt+mHkemineS4um5pbCIpCiAgICB9CiAgICBBdmFsLCBfID0gc3RyY29udi5BdG9pKHN0cmluZyhBdmFsYnl0ZXMpKQoKICAgIEJ2YWxieXRlcywgZXJyIDo9IHN0dWIuR2V0U3RhdGUoQikKICAgIGlmIGVyciAhPSBuaWwgewogICAgICAgIHJldHVybiBzaGltLkVycm9yKCLmsqHmib7liLDnrKzkuozkuKrnmoTnlKjmiLfph5Hpop0iKQogICAgfQogICAgaWYgQnZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuesrOS6jOS4queUqOaIt+mHkemineS4um5pbCIpCiAgICB9CiAgICBCdmFsLCBfID0gc3RyY29udi5BdG9pKHN0cmluZyhCdmFsYnl0ZXMpKQoKICAgIFgsIGVyciA9IHN0cmNvbnYuQXRvaShhcmdzWzJdKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIui9rOi0pumHkemineW8guW4uCIpCiAgICB9CiAgICBBdmFsID0gQXZhbCAtIFgKICAgIEJ2YWwgPSBCdmFsICsgWAogICAgZm10LlByaW50ZigiQXZhbCA9ICVkLCBCdmFsID0gJWRcbiIsIEF2YWwsIEJ2YWwpCgogICAgLy/ph43mlrDmm7TmlrDnlKjmiLfph5Hpop0KICAgIGVyciA9IHN0dWIuUHV0U3RhdGUoQSwgW11ieXRlKHN0cmNvbnYuSXRvYShBdmFsKSkpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcihlcnIuRXJyb3IoKSkKICAgIH0KCiAgICBlcnIgPSBzdHViLlB1dFN0YXRlKEIsIFtdYnl0ZShzdHJjb252Lkl0b2EoQnZhbCkpKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoZXJyLkVycm9yKCkpCiAgICB9CgogICAgcmV0dXJuIHNoaW0uU3VjY2VzcyhuaWwpCn0KCmZ1bmMgKHQgKkxHU2ltcGxlQ2hhaW5jb2RlKSBkZWxldGUoc3R1YiBzaGltLkNoYWluY29kZVN0dWJJbnRlcmZhY2UsIGFyZ3MgW11zdHJpbmcpIHBiLlJlc3BvbnNlIHsKICAgIGlmIGxlbihhcmdzKSAhPSAxIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcigi5Y+C5pWw5byC5bi4IikKICAgIH0KICAgIEEgOj0gYXJnc1swXQoKICAgIGVyciA6PSBzdHViLkRlbFN0YXRlKEEpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICByZXR1cm4gc2hpbS5FcnJvcigi5Yig6Zmk55So5oi35byC5bi4IikKICAgIH0KCiAgICByZXR1cm4gc2hpbS5TdWNjZXNzKG5pbCkKfQoKZnVuYyAodCAqTEdTaW1wbGVDaGFpbmNvZGUpIHF1ZXJ5KHN0dWIgc2hpbS5DaGFpbmNvZGVTdHViSW50ZXJmYWNlLCBhcmdzIFtdc3RyaW5nKSBwYi5SZXNwb25zZSB7CiAgICB2YXIgQSBzdHJpbmcKICAgIHZhciBlcnIgZXJyb3IKCiAgICBpZiBsZW4oYXJncykgIT0gMSB7CiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoIuafpeivouWPguaVsOW8guW4uCIpCiAgICB9CgogICAgQSA9IGFyZ3NbMF0KCiAgICBBdmFsYnl0ZXMsIGVyciA6PSBzdHViLkdldFN0YXRlKEEpCiAgICBpZiBlcnIgIT0gbmlsIHsKICAgICAgICBqc29uUmVzcCA6PSAie1wiRXJyb3JcIjpcIueUqOaIt+mHkemineafpeivouW8guW4uCAiICsgQSArICJcIn0iCiAgICAgICAgcmV0dXJuIHNoaW0uRXJyb3IoanNvblJlc3ApCiAgICB9CgogICAgaWYgQXZhbGJ5dGVzID09IG5pbCB7CiAgICAgICAganNvblJlc3AgOj0gIntcIkVycm9yXCI6XCLnlKjmiLfph5Hpop3kuLpuaWwgIiArIEEgKyAiXCJ9IgogICAgICAgIHJldHVybiBzaGltLkVycm9yKGpzb25SZXNwKQogICAgfQoKICAgIGpzb25SZXNwIDo9ICJ7XCJOYW1lXCI6XCIiICsgQSArICJcIixcIkFtb3VudFwiOlwiIiArIHN0cmluZyhBdmFsYnl0ZXMpICsgIlwifSIKICAgIGZtdC5QcmludGYoIlF1ZXJ5IFJlc3BvbnNlOiVzXG4iLCBqc29uUmVzcCkKICAgIHJldHVybiBzaGltLlN1Y2Nlc3MoQXZhbGJ5dGVzKQp9CgpmdW5jIG1haW4oKSB7CiAgICBlcnIgOj0gc2hpbS5TdGFydChuZXcoTEdTaW1wbGVDaGFpbmNvZGUpKQogICAgaWYgZXJyICE9IG5pbCB7CiAgICAgICAgZm10LlByaW50Zigi6ZO+56CB6YOo572y5aSx6LSlOiAlcyIsIGVycikKICAgIH0KfQo=";

        //param
        ReqDeployVO reqDeployVO = new ReqDeployVO();
        reqDeployVO.setChainCodeLang(TransactionRequest.Type.GO_LANG.toString());
        reqDeployVO.setChainCodeName("heih");
        reqDeployVO.setChannelName("mychannel");
        reqDeployVO.setVersion("1_3");
        reqDeployVO.setInitParams(new String[]{"a", "100", "b", "200"});
        reqDeployVO.setChainCodeSourceBase64(sourceBase64);
        String baseResponse = chainCodeService.deploy(reqDeployVO);
        System.out.println(format("deploy baseResponse:%s", JSON.toJSONString(baseResponse)));
    }

    @Test
    public void transactionTest() throws IOException, InterruptedException, TimeoutException, ContractException {


        // Obtain a smart contract deployed on the network.
       // Contract contract = network.getContract("myhello::chaincode::v1.0");//.getContract("mycc");
        Contract contract = network.getContract("mychannel_mh_1");//.getContract("mycc");

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
