package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.AdhocNode;
import cn.edu.uestc.Adhoc.entity.AdhocNodeFactory;
import cn.edu.uestc.Adhoc.utils.AdhocUtils;

import java.util.HashMap;
import java.util.Map;

public class AdhocTest {

    public static void main(String[] args) {
        AdhocNode adhocNode = AdhocNodeFactory.getInstance("usb0");
        adhocNode.setIp(1);
        adhocNode.writeThread.start();
//        int numberbefore=0xabcd;
//        adhocNode.sendRREQ(2);
//        byte[] b=AdhocUtils.IntToBytes(numberbefore);
//        int numberafter=AdhocUtils.BytesToint(b);
//        System.out.println(numberafter==numberbefore);
    }

}


