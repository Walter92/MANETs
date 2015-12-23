package cn.edu.uestc.Adhoc.entity.route;

import java.util.HashSet;

/**
 * AODV Node Routing Table Entry
 * 1. 目的节点IP地址：每个表项都具有不同的目的路由器IP地址，以此作为区别和查找网络路由的关键字；
 * 2. 目的节点序列号：可防止网络产生环路，只有当收到的RREP和RREQ中传送的序列号比本节点对应路由表项中目的序列号大时
 * 才能被接收并用于更新路由表项；
 * 3. 目的节点序列号是否正确的标志：显示路由表项中目的序列号是否有效；
 * 4. 网络接口；
 * 5. 下一跳：到达目的节点的路径上的下一跳节点的地址；
 * 6. 跳数：从本节点到达目的路由器所需要的跳数；
 * 7. 生存时间：路由过期或应当删除的时间；
 * 8. 先驱表：先驱链表指针指向使用此路由表项的所有可能的邻居节点地址；
 * 9. 其他状态和路由标志：如路由有效、无效、可修复、正在修复。
 */
public class RouteEntry {

    //目标节点的IP地址
    private int DestIP;
    //目标节点的序列号
    private int SeqNum;
    //路由表项当前状态
    private StateFlags State;
    //去往目标节点的跳数
    private int HopCount;
    //去往目标节点的下一节点的IP地址
    private int NextHopIP;

    private HashSet<Integer> PrecursorIPs = new HashSet<Integer>();

    //在这个时间内，该表项有效
    private int Lifetime;


    public RouteEntry(int destIP, int nextHopIP, int seqNum, StateFlags state, int hopCount, int lifetime) {
        DestIP = destIP;
        SeqNum = seqNum;
        State = state;
        HopCount = hopCount;
        NextHopIP = nextHopIP;
        Lifetime = lifetime;
    }

    //setter和getter方法
    public int getDestIP() {
        return DestIP;
    }

    public void setDestIP(int destIP) {
        DestIP = destIP;
    }

    public int getSeqNum() {
        return SeqNum;
    }

    public void setSeqNum(int seqNum) {
        SeqNum = seqNum;
    }

    public StateFlags getState() {
        return State;
    }

    public void setState(StateFlags state) {
        State = state;
    }

    public int getHopCount() {
        return HopCount;
    }

    public void setHopCount(int hopCount) {
        HopCount = hopCount;
    }

    public int getNextHopIP() {
        return NextHopIP;
    }

    public void setNextHopIP(int nextHopIP) {
        NextHopIP = nextHopIP;
    }

    public HashSet<Integer> getPrecursorIPs() {
        return PrecursorIPs;
    }

    public void setPrecursorIPs(HashSet<Integer> precursorIPs) {
        PrecursorIPs = precursorIPs;
    }

    public int getLifetime() {
        return Lifetime;
    }

    public void setLifetime(int lifetime) {
        Lifetime = lifetime;
    }

    @Override
    public String toString() {
        return "RouteEntry{" +
                "DestIP=" + DestIP +
                ", SeqNum=" + SeqNum +
                ", State=" + State +
                ", HopCount=" + HopCount +
                ", NextHopIP=" + NextHopIP +
                ", PrecursorIPs=" + PrecursorIPs +
                ", Lifetime=" + Lifetime +
                '}';
    }
}
