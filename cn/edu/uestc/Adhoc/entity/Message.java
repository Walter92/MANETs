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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
