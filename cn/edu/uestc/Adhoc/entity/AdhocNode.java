package cn.edu.uestc.Adhoc.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.edu.uestc.Adhoc.readAndWrite.SerialReadThread;
import cn.edu.uestc.Adhoc.readAndWrite.SerialWriteThread;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class AdhocNode implements IAdhocNode {

    public static SerialPort serialPort;
    public static CommPortIdentifier portId;
    @SuppressWarnings("rawtypes")
    public static Enumeration portList;

    //串口输入输出流
    private InputStream is;
    private OutputStream os;

    // 节点IP地址和节点端口名字
    private String ip;
    private String portName;
    //节点发出的序列号，该节点每发送出一次RREQ或者RREP时都会在该寻列号上加一，用以标识这是否是一次新的路由请求或者路由回复
    private int seqNum;
    //节点的路由表
    private Map<String, RouteEntry> routeTable = new HashMap<String, RouteEntry>();
    // 节点的处理器个数以及最大内存
    private SystemInfo systemInfo;

    // 读写线程
    public Thread readThread;
    public Thread writeThread;

    // 获取节点IP
    public String getIp() {
        return ip;
    }

//	public void setIp(String ip) {
//		this.ip = ip;
//	}

    // 节点处理器个数
    public int getProcessorCount() {
        return systemInfo.getProcessorCount();
    }

    // 节点最大内存
    public long getMemorySize() {
        return systemInfo.getMemorySize();
    }

    //初始化块，获取该节点的处理能力
    {
        Runtime rt = Runtime.getRuntime();
        // 获取主机最大内存
        systemInfo.setMemorySize(rt.maxMemory());
        // 获取主机处理器个数
        systemInfo.setProcessorCount(rt.availableProcessors());
    }

    // 通过串口名字构造一个结点
    public AdhocNode(String portName) {
        // 设置通信的串口
        this.portName = portName;
        this.seqNum=1;
        try {
            init();
        } catch (Exception e) {
        }
        // 为节点初始化收发线程
        readThread = new Thread(new SerialReadThread(is));
        readThread.start();
        System.out.println("接收线程开启，可以接收数据...");
//		writeThread = new Thread(new SerialWriteThread(os));
    }

    @Test
    // 节点初始化
    private void init() throws UnsupportedCommOperationException,
            IOException, PortInUseException {
        // 首先对电脑的可用端口进行枚举
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            // 判断端口的类型以及名字，打开需要打开的端口
            portId = (CommPortIdentifier) portList.nextElement();
            // System.out.println(portId.getName());
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portName.equals(portId.getName())) {
                    try {
                        // 打开端口，超时时间为2000
                        serialPort = (SerialPort) portId.open("Adhoc", 2000);
                        System.out.println(portName + " 串口开启成功.");
                        break;
                    } catch (PortInUseException e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }

        try {
            // 初始化输入输出流，为创建收发线程准备
            is = serialPort.getInputStream();
            os = serialPort.getOutputStream();
            System.out.println("初始化端口IO流成功！");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            // 设置初始化参数
            System.out.println("节点初始化参数设置（波特率，数据位，停止位，校验位）...");
            serialPort.setSerialPortParams(9600, // 波特率
                    SerialPort.DATABITS_8, // 数据位
                    SerialPort.STOPBITS_1, // 停止位
                    SerialPort.PARITY_NONE);// 校验位
            System.out.println("节点初始化参数设置完毕。");
        } catch (UnsupportedCommOperationException e) {
            System.out.println("端口初始化失败！");
            throw e;
        }
    }

    @Override
    public void sendRREQ(String destIP) {
        System.out.println("节点"+getIp()+"对节点"+destIP
                +"发起路由请求...");
        //本节点对目标节点发出一次RREQ，发出后把seqNum参数加一，以便下次在发出RREQ时为最新请求
        Message message=new Message(seqNum++,IAdhocNode.RREQ,ip,ip,
                destIP, systemInfo,0);
        try {
            Thread t=new Thread(new SerialWriteThread(os,message));
            t.start();
            //等待发送结束
            t.join();
            System.out.println("节点"+getIp()+"对节点"+destIP
                    +"发起路由请求成功，正在等待路由回复...");
        } catch (IOException e) {
            System.out.println("节点"+getIp()+"对节点"+destIP
                    +"发起路由请求失败，发送线程创建失败!");
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    /**
     *
     * @param message
     * 收到的信息对象
     * 首先判断本机路由表中是否有该信息中源地址的表项，如果有并且比收到的信息中的序列号大，则丢弃该信息不做处理
     * 否则新建一个路由表项，以源地址为键，如果是直接收到源节点的请求，信息中转发节点就是源节点，可以直接用于建立去往源节点
     * 的下一跳节点，建立反向路由
     */
    @Override
    public void receiveRREQ(Message message) {
        System.out.println("节点"+getIp()+"收到节点"+message.getSrcIP()
                +"对节点"+message.getDestIP()+"发起的路由请求，正在处理中...");
        String key=message.getSrcIP();
        //如果收到的信息里面，请求的序列号的键存在，并且小于等于本机所存，则抛弃
        if(routeTable.containsKey(key)&&routeTable.get(key).getSeqNum()>=message.getSeqNum()){
            return;
        }else{
            //转发该信息节点是否为null
            routeTable.put(key,new RouteEntry(key,message.getSeqNum(),StateFlags.VALID,message.getHop(),message.getRouteIP(),0));
        }
        //如果收到的信息中是寻找本机，则回复路由响应
        if(ip.equals(message.getDestIP())){
            Message message1=new Message(seqNum++,IAdhocNode.RREP,ip,ip,message.getSrcIP(),systemInfo,0);
            sendRREP(message1);
            return;
        }
        //如果信息中不是在寻找本机，则给跳数加一和更新转发节点ip后转发该请求
        message.setHop(message.getHop()+1);
        message.setRouteIP(ip);
        //转发
        forwardRREQ(message);

    }

    @Override
    public void forwardRREQ(Message message) {
        System.out.println("节点"+ip+"转发节点"+message.getSrcIP()+"对节点"+message.getDestIP()
                +"发起的路由请求...");
        try {
            Thread t=new Thread(new SerialWriteThread(os,message));
            t.start();
            //等待发送结束
            t.join();
            System.out.println("转发路由请求成功!");
        } catch (IOException e) {
            System.out.println("转发路由请求失败，发送线程创建失败!");
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Override
    public void sendRREP(Message message) {
        System.out.println("节点"+ip+"回复节点"+message.getDestIP()
                +"发起的对本节点的路由请求...");
        try {
            Thread t=new Thread(new SerialWriteThread(os,message));
            t.start();
            //等待发送结束
            t.join();
            System.out.println("路由回复成功!");
        } catch (IOException e) {
            System.out.println("路由回复失败，发送线程创建失败!");
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Override
    public void receiveRREP(Message message) {
        System.out.println("节点"+ip+"收到节点"+message.getSrcIP()
                +"对节点"+message.getDestIP()+"发起的路由回复，正在处理中...");
        String key=message.getSrcIP();
        //如果收到的信息里面，请求的序列号的键存在，并且小于等于本机所存，则抛弃
        if(routeTable.containsKey(key)&&routeTable.get(key).getSeqNum()>=message.getSeqNum()){
            return;
        }else{
            //转发该信息节点是否为null
            routeTable.put(key,new RouteEntry(key,message.getSeqNum(),StateFlags.VALID,message.getHop(),message.getRouteIP(),0));
        }
        //如果收到的信息中是寻找本机，则回复路由响应
        if(ip.equals(message.getDestIP())){
           System.out.println("本节点发起的对节点"+message.getDestIP()+"的路由请求成功，收到该节点的路由回复，该节点系统信息如下为"+
                message.getSystemInfo().toString());
            return;
        }
        //如果信息中不是在寻找本机，则给跳数加一和更新转发节点ip后转发该请求
        message.setHop(message.getHop()+1);
        message.setRouteIP(ip);
        //转发
        forwardRREQ(message);
    }

    @Override
    public void forwardRREP(Message message) {
        System.out.println("节点"+ip+"转发节点"+message.getSrcIP()+"对节点"+message.getDestIP()
                +"的路由回复...");
        try {
            Thread t=new Thread(new SerialWriteThread(os,message));
            t.start();
            //等待发送结束
            t.join();
            System.out.println("转发路由回复成功!");
        } catch (IOException e) {
            System.out.println("转发路由回复失败，发送线程创建失败!");
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Override
    public void dispatch(Message message) {
        int type=message.getType();
        switch (type){
            case IAdhocNode.RREQ: receiveRREQ(message);break;
            case IAdhocNode.RREP:receiveRREP(message);break;
        }
    }
}
