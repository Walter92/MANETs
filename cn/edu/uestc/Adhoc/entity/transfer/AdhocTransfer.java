package cn.edu.uestc.Adhoc.entity.transfer;

import cn.edu.uestc.Adhoc.entity.message.Message;

import java.io.IOException;
import java.util.EventListener;

/**
 * Created by walter on 15-12-21.
 * adhoc 的传输层接口，实现该接口可以为adhoc节点提供传输
 */
public interface AdhocTransfer {
    void recieve();
    void send(Message message) throws IOException;
    void addRecieveListener(EventListener listener);
    byte[] getMessage();
    void setMessage(byte[] message);

}
