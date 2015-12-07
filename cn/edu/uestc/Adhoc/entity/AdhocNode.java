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

    //要发送的数据和接收到的数据
    public static String messageForSend;
    public static String messageForRece;

    //串口输入输出流
    private InputStream is;
    private OutputStream os;

    // 节点IP地址和节点端口名字
    private String ip;
    private String portName;

    //节点的路由表
    private Map<String, RouteEntry> routeTable = new HashMap<String, RouteEntry>();
    //到某一目的节点的序列号，表示这是对某一节点发出了多少次请求
    //String格式为(srcIP,destIP),表示某源节点对某目的节点发出了几次请求
    private Map<String,Integer> seqNumMapRREQ=new HashMap<String, Integer>();
    //到某一目的节点的序列号，表示这是对某一节点发出了多少次响应
    //String格式为(srcIP,destIP),表示某源节点对某目的节点发出了几次响应
    private Map<String,Integer> seqNumMapRREP=new HashMap<String, Integer>();
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
        //如果从来为向该目的节点发出过RREQ则新建一个键值对
        String key=ip+","+destIP;
        if(!seqNumMapRREQ.containsKey(key)) {
            seqNumMapRREQ.put(key, 1);
        }
        System.out.println("节点"+getIp()+"对节点"+destIP
                +"发起路由...");
        Message message=new Message(seqNumMapRREQ.get(destIP),IAdhocNode.RREQ,getIp(),
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


    @Override
    public void receiveRREQ(Message message) {
        System.out.println("节点"+getIp()+"收到节点"+message.getSrcIP()
                +"对节点"+message.getDestIP()+"发起的路由请求，正在处理中...");
        String key=message.getSrcIP()+","+message.getDestIP();
        //如果收到的信息里面，请求的序列号的键存在，并且小于等于本机所存，则抛弃
        if(seqNumMapRREQ.containsKey(key)&&seqNumMapRREQ.get(key)>=message.getDataSeq()){
            return;
        }
        //如果收到的信息中是寻找本机，则回复路由响应
        if(ip.equals(message.getDestIP())){
            //如果是第一次对该节点回复路由请求，则新建条目,否则在原来的请求次数上加1
            if(!seqNumMapRREP.containsKey(key)){
                seqNumMapRREP.put(key,1);
            }else{
                seqNumMapRREP.put(key,seqNumMapRREP.get(key)+1);
            }
            //新建一个RREP信息回复
            Message message1=new Message(seqNumMapRREP.get(key),IAdhocNode.RREP,ip,message.getSrcIP(),systemInfo,0);
            sendRREP(message1);
            return;
        }
        //如果信息中不是在寻找本机，则给跳数加一后转发该请求
        message.setHop(message.getHop()+1);
        forwardRREQ(message);

    }

    @Override
    public void forwardRREQ(Message message) {

    }

    @Override
    public void sendRREP(Message message) {

    }

    @Override
    public void receiveRREP(Message message) {

    }

    @Override
    public void forwardRREP(Message message) {

    }
}
