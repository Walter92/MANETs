package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

public class MessageRREQ extends Message implements Serializable{
    static  final long seriaVersionUID = 146346543L;
    //转发节点的IP
    private String routeIP;
    //系统信息，包含了处理器个数以及内存大小
    private SystemInfo systemInfo;
    //跳数
    private  int hop;

    public MessageRREQ() {
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
