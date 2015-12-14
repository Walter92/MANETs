package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

public class MessageRREP extends Message implements Serializable{
    static  final long seriaVersionUID = 168765L;
    //转发节点的IP
    private String routeIP;
    //发送的数据长度
    //系统信息，包含了处理器个数以及内存大小
    private SystemInfo systemInfo;
    //跳数
    private  int hop;
    /**
     * 数据序列号，如果收到的数据小于或者等于节点存储的序列号时，则抛弃该数据，不做处理，避免形成广播风暴
     *比如说A节点广播了某一次路由请求，B节点收到该请求再次广播，则A节点就会收到该广播，所以通过该属性来判断这次数据帧是同一次广播，进而不做理会
     */
    protected int seqNum;
    public MessageRREP() {
    }

    public String getRouteIP() {
        return routeIP;
    }

    public void setRouteIP(String routeIP) {
        this.routeIP = routeIP;
    }

    public int getHop() {
        return hop;
    }

    public void setHop(int hop) {
        this.hop = hop;
    }


    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

}
