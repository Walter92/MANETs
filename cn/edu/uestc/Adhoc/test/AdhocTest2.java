package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.*;
import cn.edu.uestc.Adhoc.readAndWrite.SerialWriteThread;

/**
 * Created by walter on 15-12-14.
 */
public class AdhocTest2 {
    public static void main(String[] args) throws Exception{
        AdhocNode adhocNode = AdhocNodeFactory.getInstance("usb0");
        adhocNode.setIp("abc");
        Message messageRREP=new MessageRREP();
        messageRREP.setType(RouteProtocol.RREP);
        messageRREP.setSrcIP(adhocNode.getIp());
        messageRREP.setDestIP("def");
        new Thread(new SerialWriteThread(adhocNode.getOs(),messageRREP)).start();
    }
}
