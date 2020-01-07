import com.webank.fabric.front.Application;
import com.webank.fabric.front.sdk.SdkService;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SDKServiceTest {

    @Autowired
    private SdkService sdkService;


    @Test
    public void queryBlockByNumberTest() throws InvalidArgumentException, ProposalException, IOException {
        byte[] blockString = sdkService.queryBlockByNumber(2L);
        Common.Block block = Common.Block.parseFrom(blockString);
        //BlockInfo blockInfo1 = FrontUtils.getInstanceByReflection(BlockInfo.class,Arrays.asList(block));

    }


}

