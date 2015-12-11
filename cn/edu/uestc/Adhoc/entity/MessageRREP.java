package cn.edu.uestc.Adhoc.entity;

public class MessageRREP extends Message {
    static  final long seriaVersionUID = 168765L;
    //转发节点的IP
    private String routeIP;
    //发送的数据长度
    //系统信息，包含了处理器个数以及内存大小
    private SystemInfo systemInfo;
    //跳数
    private  int hop;

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
