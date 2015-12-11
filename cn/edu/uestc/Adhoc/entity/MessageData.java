package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

/**
 * Created by walter on 15-12-11.
 */
public class MessageData extends Message implements Serializable{
    static  final long seriaVersionUID = 16876554L;
    //去往目标节点的下一跳节点地址
    private String nextIP;
    //数据长度
    private int dataLen;
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

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
