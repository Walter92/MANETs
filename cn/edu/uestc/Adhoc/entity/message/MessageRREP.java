package cn.edu.uestc.Adhoc.entity.message;


import cn.edu.uestc.Adhoc.entity.route.RouteProtocol;
import cn.edu.uestc.Adhoc.entity.systeminfo.SystemInfo;
import cn.edu.uestc.Adhoc.utils.MessageUtils;

public class MessageRREP extends Message {
    //转发节点的IP
    private int routeIP;
    //发送的数据长度
    //系统信息，包含了处理器个数以及内存大小
    private SystemInfo systemInfo;
    //跳数
    private byte hop;
    /**
     * 数据序列号，如果收到的数据小于或者等于节点存储的序列号时，则抛弃该数据，不做处理，避免形成广播风暴
     * 比如说A节点广播了某一次路由请求，B节点收到该请求再次广播，则A节点就会收到该广播，所以通过该属性来判断这次数据帧是同一次广播，进而不做理会
     */
    protected byte seqNum;

    public MessageRREP() {
    }

    public MessageRREP(int routeIP, byte hop, byte seqNum, SystemInfo systemInfo) {
        this.routeIP = routeIP;
        this.systemInfo = systemInfo;
        this.hop = hop;
        this.seqNum = seqNum;
    }

    public int getRouteIP() {
        return routeIP;
    }

    public void setRouteIP(int routeIP) {
        this.routeIP = routeIP;
    }

    public byte getHop() {
        return hop;
    }

    public void setHop(byte hop) {
        this.hop = hop;
    }

    public byte getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(byte seqNum) {
        this.seqNum = seqNum;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public byte[] getBytes() {
        byte[] srcByte = MessageUtils.IntToBytes(getSrcIP());
        byte[] routeByte = MessageUtils.IntToBytes(routeIP);
        byte[] destByte = MessageUtils.IntToBytes(getDestIP());
        byte[] sysByte = systemInfo.getBytes();
        byte[] messageByte = {
                RouteProtocol.frameHeader[0], RouteProtocol.frameHeader[1],//帧头,0,1
                RouteProtocol.RREP,//数据类型,2
                srcByte[0], srcByte[1],//源节点,3,4
                routeByte[0], routeByte[1],//转发节点,5,6
                destByte[0], destByte[1],//目标节点7,8
                seqNum,//序列号,9
                hop,//跳数,10
                sysByte[0], sysByte[1],//系统信息,11,12
                RouteProtocol.frameEnd[0], RouteProtocol.frameEnd[1]//帧尾,13,14
        };
        return messageByte;
    }

    //将byte数组转化为Message对象
    public static MessageRREP recoverMsg(byte[] bytes) {
        ///恢复byte数组中的数据
        int srcIP = MessageUtils.BytesToInt(new byte[]{bytes[3], bytes[4]});
        int routeIP = MessageUtils.BytesToInt(new byte[]{bytes[5], bytes[6]});
        int destIP = MessageUtils.BytesToInt(new byte[]{bytes[7], bytes[8]});
        byte seqNum = bytes[9];
        byte hop = bytes[10];
        SystemInfo sysInfo = SystemInfo.recoverSysInfo(new byte[]{bytes[11], bytes[12]});

        MessageRREP message = new MessageRREP(routeIP, hop, seqNum, sysInfo);
        message.setSrcIP(srcIP);
        message.setDestIP(destIP);
        message.setType(RouteProtocol.RREP);
        return message;
    }
}