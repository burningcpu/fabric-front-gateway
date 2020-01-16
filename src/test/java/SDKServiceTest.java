import com.google.protobuf.InvalidProtocolBufferException;
import com.webank.fabric.front.Application;
import com.webank.fabric.front.commons.utils.FrontUtils;
import com.webank.fabric.front.sdk.SdkService;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SDKServiceTest {


    @Autowired
    private SdkService sdkService;


    @Test
    public void queryBlockByNumberTest() throws InvalidArgumentException, ProposalException, IOException {
        byte[] blockString = sdkService.queryBlockByNumber(2L);
        Common.Block block = Common.Block.parseFrom(blockString);
        BlockInfo blockInfo1 = FrontUtils.getInstanceByReflection(BlockInfo.class, Arrays.asList(block));

    }

    @Test
    public void queryBlockByTransaction() throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
        String txId = "7a0b0875b2b49e72932c5dc7608aa4d4d40eca927043470fdf3ba8711e9ece8d";
        byte[] blockString = sdkService.queryBlockByTransactionId(txId);
        Common.Block block = Common.Block.parseFrom(blockString);
        BlockInfo blockInfo = FrontUtils.getInstanceByReflection(BlockInfo.class, Arrays.asList(block));
        for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {

            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                String txIdd = transactionEnvelopeInfo.getTransactionID();
                System.out.println("=========txId:" + txIdd);

//                String channelId = transactionEnvelopeInfo.getChannelId();
//                System.out.println("=========channelId:"+channelId);
//                TransactionInfo transaction = channel.queryTransactionByID(txId);
//                Common.Envelope envelope = transaction.getEnvelope();
//                String signature = Hex.encodeHexString(envelope.getSignature().toByteArray());
//                System.out.println("=========signature:"+signature);
//                System.out.println("=========creator:"+envelopeInfo.getCreator().getId().substring(0,60));


                Iterable<BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo> TransactionEnvelopeInfo = ((BlockInfo.TransactionEnvelopeInfo) envelopeInfo).getTransactionActionInfos();
                for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo i : TransactionEnvelopeInfo) {

                    System.out.println("=========chainCodeIDName:" + i.getChaincodeIDName());
                    System.out.println("=========chainCodeIDPath:" + i.getChaincodeIDPath());
                    System.out.println("=========chainCodeIDVersion:" + i.getChaincodeIDVersion());

                    System.out.println("=========chainCodeInputArgsCount:" + i.getChaincodeInputArgsCount());
                    StringBuffer argb = new StringBuffer();
                    for (int m = 0; m < i.getChaincodeInputArgsCount(); m++) {
                        argb.append(" ").append(new String(i.getChaincodeInputArgs(m)));
                    }
                    System.out.println("=========chainCodeInputArgs:" + argb.toString());
                    System.out.println("=========responseMessage:" + i.getResponseMessage());
                    System.out.println("=========responseStatus:" + i.getResponseStatus());


                    System.out.println("=========endorsementsCount:" + i.getEndorsementsCount());
                    for (int m = 0; m < i.getEndorsementsCount(); m++) {
                        BlockInfo.EndorserInfo str = i.getEndorsementInfo(m);
                        System.out.println("=========endorsementInfo" + m + ":" + i.getEndorsementInfo(m).getId().substring(0, 60));
                    }
                }

            }
        }
    }


}

