package cn.edu.uestc.Adhoc.readAndWrite;

import cn.edu.uestc.Adhoc.entity.Message;

import java.io.*;
import java.util.Objects;

public class SerialWriteThread implements Runnable {

    // 数据输出流
    private OutputStream os;
    private BufferedOutputStream bos;
    private ObjectOutputStream objectOutputStream;
    private Message message;

    public SerialWriteThread(OutputStream os, Message message) throws  IOException{
        this.os = os;
        this.bos = new BufferedOutputStream(this.os);
        objectOutputStream = new ObjectOutputStream(os);
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println("开始发送数据...");
        while (true) {
            try {
                bos.write("hello".getBytes());
                bos.flush();
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                Thread.sleep(1000);
            } catch (IOException e) {
                System.out.println("发送异常...");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
