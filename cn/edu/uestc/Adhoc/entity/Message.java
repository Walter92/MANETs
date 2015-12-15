package cn.edu.uestc.Adhoc.entity;

/**
 * Created by walter on 15-12-11.
 */
public abstract class Message {
    //信息的类型，便于分发消息处理
    protected int type;
    //源节点的IP
    protected int srcIP;
    //目的节点的IP
    protected int destIP;

    public int getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(int srcIP) {
        this.srcIP = srcIP;
    }

    public int getDestIP() {
        return destIP;
    }

    public void setDestIP(int destIP) {
        this.destIP = destIP;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public abstract byte[] getBytes();
}
