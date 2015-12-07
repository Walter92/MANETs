package cn.edu.uestc.Adhoc.readAndWrite;

import cn.edu.uestc.Adhoc.entity.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialReadThread implements Runnable, SerialPortEventListener {
	private InputStream is;
	private Thread readThread;

	private BufferedInputStream bis;

	public SerialReadThread(InputStream is) {
		this.is = is;
		bis = new BufferedInputStream(this.is);

		try {
			// 在节点上注册事件监听器
			System.out.println("为串口注册事件监听...");
			AdhocNode.serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} 
		// 通知数据可用，开始读数据
		AdhocNode.serialPort.notifyOnDataAvailable(true);
		readThread = new Thread(this);
		readThread.start();
	}

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
				while (is.available() > 0) {
					numBytes = is.read(buf);
					//将读取到的数据输出到控制台
				}
				System.out.print("收到数据:");
                String message=new String(buf,0,numBytes);
				System.out.println(message);

			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
