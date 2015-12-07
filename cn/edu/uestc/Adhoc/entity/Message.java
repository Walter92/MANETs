package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

public class Message implements Serializable{
    static  final long seriaVersionUID = 1L;
    /**
     * 数据序列号，如果收到的数据小于或者等于节点存储的序列号时，则抛弃该数据，不做处理，避免形成广播风暴
     *比如说A节点广播了某一次路由请求，B节点收到该请求再次广播，则A节点就会收到该广播，所以通过该属性来判断这次数据帧是同一次广播，进而不做理会
     */
    private int dataSeq;
    //数据类型
    private int type;
    //源节点的IP
    private String srcIP;
    //目的节点的IP
    private String destIP;
    //发送的数据长度
    private int dataLen = 0;
    //系统信息，包含了处理器个数以及内存大小
    private SystemInfo systemInfo;
    private byte[] datagram = null;
    //跳数
    private  int hop;

    public Message() {
    }

    public Message(int dataSeq, int type, String srcIP, String destIP, SystemInfo systemInfo, int hop) {
        this.dataSeq = dataSeq;
        this.type = type;
        this.srcIP = srcIP;
        this.destIP = destIP;
        this.systemInfo = systemInfo;
        this.hop = hop;
    }

    public int getHop() {
        return hop;
    }

    public void setHop(int hop) {
        this.hop = hop;
    }

    public byte[] getDatagram() {
        return datagram;
    }

    public void setDatagram(byte[] datagram) {
        this.datagram = datagram;
    }

    public int getDataSeq() {
        return dataSeq;
    }

    public void setDataSeq(int dataSeq) {
        this.dataSeq = dataSeq;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDestIP() {
        return destIP;
    }

    public void setDestIP(String destIP) {
        this.destIP = destIP;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }
}
