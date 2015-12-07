package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.AdhocNode;
import cn.edu.uestc.Adhoc.entity.AdhocNodeFactory;

public class AdhocTest2 {

	public static void main(String[] args) {
        AdhocNode adhocNode= AdhocNodeFactory.getInstance("usb0");
		adhocNode.writeThread.start();
//        String x="ff";
//        System.out.print(Long.valueOf(x, 16));
	}
}
