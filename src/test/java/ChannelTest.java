import com.webank.fabric.front.Application;
import com.webank.fabric.front.commons.utils.FrontUtils;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ChannelTest {
    private static Channel channel;
    private static Network network;

    @Qualifier(value = "localNetwork")
    @Autowired
    public void setChannel(Network network) {
        Objects.requireNonNull(network, "init SdkService fail. network is null");
        this.network = network;
        this.channel = network.getChannel();
    }


    @Test
    public void transactionTest() throws IOException, InterruptedException, TimeoutException, ContractException {
        // Obtain a smart contract deployed on the network.
        Contract contract = network.getContract("mycc");

        // Evaluate transactions that query state from the ledger.
        byte[] queryResult = contract.createTransaction("query")
                .submit("a");
        System.out.println("+++++++++++++++++++++++++++++query result:"+new String(queryResult, StandardCharsets.UTF_8));

        // Submit transactions that store state to the ledger.
        byte[] invokeResult = contract.createTransaction("invoke")
                .submit("a", "b", "10");
        System.out.println("+++++++++++++++++++++++++++++send result:"+new String(invokeResult, StandardCharsets.UTF_8));

    }

}
