package cn.edu.uestc.Adhoc.entity;

/**
 * Created by walter on 15-12-11.
 */
public class MessageData extends Message {
    //去往目标节点的下一跳节点地址
    private String nextIP;
    //数据长度
    private int daraLen;
    //数据的内容
    private byte[] content;

    public MessageData() {
    }

    public String getNextIP() {
        return nextIP;
    }

    public void setNextIP(String nextIP) {
        this.nextIP = nextIP;
    }

    public int getDaraLen() {
        return daraLen;
    }

    public void setDaraLen(int daraLen) {
        this.daraLen = daraLen;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
