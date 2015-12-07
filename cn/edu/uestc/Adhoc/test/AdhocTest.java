package cn.edu.uestc.Adhoc.test;

import cn.edu.uestc.Adhoc.entity.AdhocNode;

import java.util.HashMap;
import java.util.Map;

public class AdhocTest {

	public static void main(String[] args) {
//		new AdhocNode("/dev/ttyUSB0").readThread.start();

        Map<String,Integer> map=new HashMap<String, Integer>();
        map.put("abc",3);
        map.put("abc",6);

        for(String key:map.keySet())
            System.out.print(map.get(key)+"\n");
	}

	
}
