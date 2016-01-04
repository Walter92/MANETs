package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.adhocNode.AdhocNode;
import cn.edu.uestc.Adhoc.entity.factory.AdhocNodeFactory;
import cn.edu.uestc.Adhoc.entity.message.Message;
import cn.edu.uestc.Adhoc.utils.MessageUtils;

import java.util.Arrays;

/**
 * Created by walter on 15-12-14.
 */
public class AdhocTest2 {
    public static void main(String[] args) throws Exception {
//        AdhocNode adhocNode = AdhocNodeFactory.getInstance("usb1");
//        adhocNode.setIp(2);
//        adhocNode.writeThread.start();
//        adhocNode.setIp("abc");
//        adhocNode.sendRREP(1);
//        Message messageRREP=new MessageRREP();
//        messageRREP.setType(RouteProtocol.RREP);
//        messageRREP.setSrcIP(adhocNode.getIp());
//        messageRREP.setDestIP("def");
//        new Thread(new SerialWriteThread(adhocNode.getOs(),messageRREP)).start();
        byte[] bytes = MessageUtils.IntToBytes(4323);
        System.out.println(Arrays.toString(bytes));
        System.out.println(MessageUtils.BytesToInt(bytes));
//        System.out.print(0x1f4);

    }
}
