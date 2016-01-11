package cn.edu.uestc.Adhoc.adhocThread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TooManyListenersException;

import cn.edu.uestc.Adhoc.entity.serial.Serial;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialReadThread implements Runnable, SerialPortEventListener {

    private Serial serial;
    private InputStream is;
    private BufferedInputStream bis;
    private Thread readThread;

    public SerialReadThread(Serial serial) {
        this.serial = serial;
        this.is = serial.getIs();
        bis = new BufferedInputStream(this.is);
        try {
            // 在节点上注册事件监听器
            System.out.println("初始化对象输入流成功！！");
            System.out.println("为串口注册事件监听...");
            serial.serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("初始化对象输入流失败！！");
            e.printStackTrace();
        }
        // 通知数据可用，开始读数据
        serial.serialPort.notifyOnDataAvailable(true);
        readThread = new Thread(this);
        readThread.start();
        System.out.println("接收线程初始化完毕！");
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
                byte[] buf = new byte[1024 * 4];
                byte[] bytes = null;
                try {
                    int numBytes = -1;
                    while ((numBytes = is.available()) > 0) {
                        is.read(buf,0,numBytes);
                        bytes= Arrays.copyOfRange(buf, 0, numBytes);
                  //将读取到的数据输出到控制台
                        System.out.print("收到数据:");
                        serial.setMessage(bytes);
                        System.out.println(new String(buf, 0, numBytes)+"::"+numBytes);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
