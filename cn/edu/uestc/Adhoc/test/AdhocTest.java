package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.adhocNode.AdhocNode;
import cn.edu.uestc.Adhoc.entity.factory.AdhocNodeFactory;

public class AdhocTest {

    public static void main(String[] args) {
        AdhocNode adhocNode = AdhocNodeFactory.getInstance("usb0");
        try{
            Thread.sleep(1000);
        }catch (Exception e){}
        adhocNode.getSerial().setMessage("hello".getBytes());
        System.out.println(adhocNode);
        System.out.println("ip"+adhocNode.getIp());
        System.out.println(adhocNode.getMemorySize());
        System.out.println(adhocNode.getProcessorCount());
        System.out.println(adhocNode.getSerial());
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            adhocNode.getSerial().setMessage("hello".getBytes());
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


