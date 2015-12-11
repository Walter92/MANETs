package cn.edu.uestc.Adhoc.entity;

/**
 * Created by walter on 15-12-11.
 */
public abstract class Message {
    //信息的类型，便于分发消息处理
    protected String type;


    //源节点的IP
    protected String srcIP;
    //目的节点的IP
    protected String destIP;
    /**
     * 数据序列号，如果收到的数据小于或者等于节点存储的序列号时，则抛弃该数据，不做处理，避免形成广播风暴
     *比如说A节点广播了某一次路由请求，B节点收到该请求再次广播，则A节点就会收到该广播，所以通过该属性来判断这次数据帧是同一次广播，进而不做理会
     */
    protected int    seqNum;

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

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
