package cn.edu.uestc.Adhoc.business;

import org.junit.Test;

import cn.edu.uestc.Adhoc.entity.*;

public class Routing {
	@Test
	public void test(){
		AdhocNode adhocNode=AdhocNodeFactory.getInstance("usb0");
		adhocNode.readThread.start();
	}
}
