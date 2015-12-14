package cn.edu.uestc.Adhoc.readAndWrite;

import cn.edu.uestc.Adhoc.entity.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.TooManyListenersException;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialReadThread implements Runnable, SerialPortEventListener {
	private InputStream is;
	private Thread readThread;

	private BufferedInputStream bis;
    private ObjectInputStream ois;

	public SerialReadThread(InputStream is) {
		this.is = is;
		bis = new BufferedInputStream(this.is);
		try {
			// 在节点上注册事件监听器
//            ois = new ObjectInputStream(is);
            System.out.println("初始化对象输入流成功！！");
			System.out.println("为串口注册事件监听...");
			AdhocNode.serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (Exception e) {
            System.out.println("初始化对象输入流失败！！");
            e.printStackTrace();
        }
        // 通知数据可用，开始读数据
		AdhocNode.serialPort.notifyOnDataAvailable(true);
		readThread = new Thread(this);
		readThread.start();
        System.out.println("接收线程初始化完毕！");
	}
//    public SerialReadThread(InputStream is)
//    {
//        try {
//            ois = new ObjectInputStream(is);
//            System.out.println("初始化对象输入流成功！！");
//        }catch (IOException e){
//            System.out.println("初始化对象输入流失败！！");
//            throw new RuntimeException(e);
//        }
//    }
	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 节点事件
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {// 根据时间类型做出相应反应
		case SerialPortEvent.BI:// 通讯中断
		case SerialPortEvent.OE:// 溢位错误
		case SerialPortEvent.FE:// 传帧错误
		case SerialPortEvent.PE:// 校验错误
		case SerialPortEvent.CD:// 载波检测
		case SerialPortEvent.CTS:// 清除发送
		case SerialPortEvent.DSR:// 数据设备准备就绪
		case SerialPortEvent.RI:// 响铃指示
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:// 输出缓冲区清空
			break;
		case SerialPortEvent.DATA_AVAILABLE:// 数据到达
			byte[] buf = new byte[1024];
			try {
				int numBytes = 0;
                Message message=null;
				while (is.available() > 0) {
					numBytes = is.read(buf);
				//	将读取到的数据输出到控制台
//                    try {
//                        message = (Message) ois.readObject();
//                    }catch (ClassNotFoundException cfe){}
				}
				System.out.print("收到数据:");
                String messageInfo=new String(buf,0,numBytes);
				System.out.println(messageInfo);
                if(message!=null)
                    System.out.println("源IP："+message.getSrcIP()+",目的IP"+message.getDestIP());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
