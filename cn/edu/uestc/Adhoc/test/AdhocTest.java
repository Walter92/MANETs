package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.adhocNode.AdhocNode;
import cn.edu.uestc.Adhoc.entity.factory.AdhocNodeFactory;
import cn.edu.uestc.Adhoc.entity.message.Message;
import cn.edu.uestc.Adhoc.entity.message.MessageRREP;
import cn.edu.uestc.Adhoc.entity.systeminfo.SystemInfo;

public class AdhocTest {

    public static void main(String[] args) {
        AdhocNode adhocNode = AdhocNodeFactory.getInstance("usb0");
        adhocNode.setIp(1);
        System.out.println("ip"+adhocNode.getIp());
        System.out.println(adhocNode.getMemorySize());
        System.out.println(adhocNode.getProcessorCount());
        System.out.println(adhocNode.getSerial());
        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        MessageRREP messageRREP = new MessageRREP(1,2,2,new SystemInfo(2,41911));
        messageRREP.setSrcIP(6);
        messageRREP.setDestIP(1);
        System.out.println(adhocNode);

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            adhocNode.getSerial().setMessage(messageRREP.getBytes());

        }
//        adhocNode.setIp(1);
//        adhocNode.writeThread.start();
//        int numberbefore=0xabcd;
//        adhocNode.sendRREQ(2);
//        byte[] b=AdhocUtils.IntToBytes(numberbefore);
//        int numberafter=AdhocUtils.BytesToint(b);
//        System.out.println(numberafter==numberbefore);
    }
}


