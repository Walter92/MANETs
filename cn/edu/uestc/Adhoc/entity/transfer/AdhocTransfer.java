package cn.edu.uestc.Adhoc.entity.transfer;

import cn.edu.uestc.Adhoc.entity.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EventListener;

/**
 *
 * adhoc 的传输层接口，实现该接口可以为adhoc节点提供传输
 *
 */
public interface AdhocTransfer {
    void receive();
    void send(Message message) throws IOException;
    void addReceiveListener(EventListener listener);
    byte[] getMessage();
    void setMessage(byte[] message);
    OutputStream getOs();
    InputStream getIs();

}
