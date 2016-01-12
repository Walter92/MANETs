package cn.edu.uestc.Adhoc.entity.adhocNode;

import cn.edu.uestc.Adhoc.entity.message.*;
import cn.edu.uestc.Adhoc.entity.route.RouteEntry;
import cn.edu.uestc.Adhoc.entity.route.RouteProtocol;
import cn.edu.uestc.Adhoc.entity.route.StateFlags;
import cn.edu.uestc.Adhoc.entity.serial.Serial;
import cn.edu.uestc.Adhoc.entity.serial.SerialPortEvent;
import cn.edu.uestc.Adhoc.entity.serial.SerialPortListener;
import cn.edu.uestc.Adhoc.entity.systeminfo.SystemInfo;
import cn.edu.uestc.Adhoc.entity.transfer.AdhocTransfer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统信息的记录暂时还没有得到利用。
 * 初始想法：从一个节点到另一个节点会经过多个节点是吧？所以整个路由路径上性能最低的节点就是木桶中最低的一块木板，所以只要记录
 * 路径中性能最差的一个节点的信息，就可以知道这条路径上能发送多大的数据。因此在转发路由请求的过程中，每一个节点检查请求中的消
 * 息中的最低节点性能信息，通过和自身比较，如果自身低于该性能，则重新设置最低性能信息，之后再转发出去。ps：路由请求信息应该还
 * 要增加一个字段来描述整个路由路径中最低节点的性能信息。
 * <p/>
 * 想要设置路由表项的有效时间，可以在新建该表项时增加一个时间戳，当访问该表项时带着时间戳去访问，如果当前时间大于了表项的时间戳
 * n（设置的失效时间）则该表项被判定为无效，应该从路由表中删除。
 */
public class AdhocNode implements IAdhocNode, SerialPortListener {
    //轮训次数
    private final static int POLLING_COUNT=5;

    //每次轮训的定时时间
    private static final int POLING_TIMER=1000;

    //自主网节点使用的串口对象，同时也是要监听的时间源
    private AdhocTransfer adhocTransfer;

    // 节点IP地址
    private int ip;

    //和开启的串口端口名字
    private String portName;

    //节点发出的序列号，该节点每发送出一次RREQ或者RREP时都会在该寻列号上加一，用以标识这是否是一次新的路由请求或者路由回复
    private byte seqNum;

    //节点的路由表,使用同步的路由表
    private Map<Integer, RouteEntry> routeTable =  new ConcurrentHashMap<Integer, RouteEntry>();

    //先驱列表，存储了本节点周围的节点地址，其存在的目的主要用于路由维护
    private HashSet<Integer> precursorIPs = (HashSet<Integer>)Collections.synchronizedCollection(new HashSet<Integer>());

    //接收到的hello报文的发送者队列，当收到某hello报文时将其加入到队列中，路由维护线程从对列中取出数据，用于更新路由表项的生存时间
    private Queue<Integer> helloIP = new ArrayDeque<Integer>();

    // 节点的处理器个数以及最大内存
    private SystemInfo systemInfo = new SystemInfo();

    //获取路由表,测试用
    public Map<Integer,RouteEntry> getRouteTable(){
        return this.routeTable;
    }

    // 获取节点IP
    public int getIp() {
        return ip;
    }

    //设置节点ip
    public void setIp(int ip) {
        this.ip = ip;
    }

    public AdhocTransfer getSerial() {
        return this.adhocTransfer;
    }

    // 节点系统信息
    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public HashSet<Integer> getPrecursorIPs() {
        return precursorIPs;
    }

    public void setPrecursorIPs(HashSet<Integer> precursorIPs) {
        this.precursorIPs = precursorIPs;
    }

    // 通过串口名字构造一个结点
    public AdhocNode(String portName) {
        // 设置通信的串口
        this.portName = portName;
        this.seqNum = 1;
        adhocTransfer = new Serial(portName);
        //节点对串口进行监听
        adhocTransfer.addReceiveListener(this);
        System.out.println("节点监听串口状态...");
        adhocTransfer.recieve();
        System.out.println("节点接收线程开启，等待数据到来...");
    }

    //当串口中数据被更新后执行的方法
    //adhocTransfer中message属性被更新后执行
    @Override
    public void doSerialPortEvent(SerialPortEvent serialPortEvent) {
        this.dataParsing(adhocTransfer.getMessage());
    }

    //发起对某节点的路由请求
    @Override
    public void sendRREQ(int destIP) {
        System.out.println("节点" + getIp() + "对节点" + destIP
                + "发起路由请求...");
        //本节点对目标节点发出一次RREQ，发出后把seqNum参数加一，以便下次在发出RREQ时为最新请求
        MessageRREQ messageRREQ = new MessageRREQ();
        messageRREQ.setType(RouteProtocol.RREQ);
        messageRREQ.setRouteIP(ip);
        messageRREQ.setRouteIP(destIP);
        messageRREQ.setSeqNum(seqNum++);
        messageRREQ.setSrcIP(ip);
        messageRREQ.setSystemInfo(systemInfo);
        messageRREQ.setHop((byte) 0);
        try {
            adhocTransfer.send(messageRREQ);
            System.out.println("节点" + getIp() + "对节点" + destIP
                    + "发起路由请求成功，正在等待路由回复...");
        } catch (IOException e) {
            System.out.println("节点" + getIp() + "对节点" + destIP
                    + "发起路由请求失败，发送线程创建失败!");
        }
    }

    /**
     * @param messageRREQ
     * 收到的信息对象
     * 首先判断本机路由表中是否有该信息中源地址的表项，如果有并且比收到的信息中的序列号大，则丢弃该信息不做处理
     * 否则新建一个路由表项，以源地址为键，如果是直接收到源节点的请求，信息中转发节点就是源节点，可以直接用于建立去往源节点
     * 的下一跳节点，建立反向路由
     */
    //在数据类型方法解析后调用，开始解析数据中内容，判断是否为发送给自己的RREQ
    @Override
    public void receiveRREQ(MessageRREQ messageRREQ) {
        System.out.println("节点" + getIp() + "收到节点" + messageRREQ.getSrcIP()
                + "对节点" + messageRREQ.getDestIP() + "发起的路由请求，正在处理中...");
        //将转发该消息的节点地址加入到先驱列表中
        precursorIPs.add(messageRREQ.getRouteIP());
        int key = messageRREQ.getSrcIP();
        //如果收到的信息里面，请求的序列号的键存在，并且小于等于本机所存，则抛弃
        if (routeTable.containsKey(key) && routeTable.get(key).getSeqNum() >= messageRREQ.getSeqNum()) {
            System.out.println("旧路由序列号，抛弃...");
            return;
        } else {
            //更新自己的路由表，路由表项的信息为该信息的源节点为目的地址，去往该目的地址的下一跳节点即为转发该信息的节点
            //RouteEntry(String destIP, int seqNum, StateFlags state, int hopCount, String nextHopIP, int lifetime)
            routeTable.put(key, new RouteEntry(key, messageRREQ.getRouteIP(),
                    messageRREQ.getSeqNum(), StateFlags.VALID, messageRREQ.getHop(), 0,messageRREQ.getSystemInfo()));
        }
        //如果收到的信息中是寻找本机，则回复路由响应
        if (ip == (messageRREQ.getDestIP())) {
            System.out.println("本节点收到节点" + messageRREQ.getSrcIP()
                    + "的路由请求，该节点系统信息如下:\n" +
                    messageRREQ.getSystemInfo().toString());

            MessageRREP messageRREP = new MessageRREP(ip,0,seqNum++,systemInfo);

            messageRREP.setSrcIP(ip);
            messageRREP.setDestIP(messageRREQ.getSrcIP());
            sendRREP(messageRREP);
            return;
        }
        RouteEntry routeEntry=queryRouteTable(messageRREQ.getDestIP());
        //如果本机有到要寻找的目的节点的路由，则回复路由请求
        if(routeEntry!=null){
            System.out.println("本机存储有到节点"+messageRREQ.getDestIP()+"的路由，"+
            "回复路由请求。");
            MessageRREP messageRREP = new MessageRREP(ip,0,seqNum++,routeEntry.getSystemInfo());
            messageRREP.setDestIP(messageRREQ.getSrcIP());
            messageRREP.setSrcIP(messageRREQ.getDestIP());
            sendRREP(messageRREP);
            return;
        }
        //如果信息中不是在寻找本机，则给跳数加一和更新转发节点ip后转发该请求
        messageRREQ.setHop((byte) (messageRREQ.getHop() + 1));
        messageRREQ.setRouteIP(ip);
        //转发
        forwardRREQ(messageRREQ);

    }

    //如果不是发送给自己的RREQ则将其转发出去
    @Override
    public void forwardRREQ(MessageRREQ messageRREQ) {
        System.out.println("节点" + ip + "转发节点" + messageRREQ.getSrcIP() + "对节点" + messageRREQ.getDestIP()
                + "发起的路由请求...");
        try {
            adhocTransfer.send(messageRREQ);
            System.out.println("转发路由请求成功!");
        } catch (IOException e) {
            System.out.println("转发路由请求失败，发送线程创建失败!");
        }
    }

    //对请求自己路由回复路由请求
    @Override
    public void sendRREP(MessageRREP messageRREP) {
        System.out.println("节点" + ip + "回复节点" + messageRREP.getDestIP() + "的路由请求...");
        try {
            adhocTransfer.send(messageRREP);
            System.out.println("路由回复成功!");
        } catch (IOException e) {
            System.out.println("路由回复失败!");
        }
    }

    //在数据类型方法解析后调用，开始解析数据中内容，判断是否为发送给自己的RREP
    @Override
    public void receiveRREP(MessageRREP messageRREP) {
        System.out.println("节点" + ip + "收到节点" + messageRREP.getSrcIP()
                + "对节点" + messageRREP.getDestIP() + "发起的路由回复，正在处理中...");
        //将转发该消息的节点地址加入到先驱列表中
        precursorIPs.add(messageRREP.getRouteIP());
        int key = messageRREP.getSrcIP();
        //如果收到的信息里面，请求的序列号的键存在，并且小于等于本机所存，则抛弃
        if (routeTable.containsKey(key) && routeTable.get(key).getSeqNum() >= messageRREP.getSeqNum()) {
            System.out.println("旧路由序列号，抛弃...");
            return;
        } else {
            routeTable.put(key, new RouteEntry(key, messageRREP.getRouteIP()
                    , messageRREP.getSeqNum(), StateFlags.VALID, messageRREP.getHop(), 0,messageRREP.getSystemInfo()));
        }
        //如果收到的信息中是寻找本机，则回复路由响应
        if (ip == messageRREP.getDestIP()) {
            System.out.println("本节点发起的对节点" + messageRREP.getSrcIP()
                    + "的路由请求成功，\n收到该节点的路由回复，该节点系统信息如下:\n" +
                    messageRREP.getSystemInfo().toString());
            return;
        }
        //如果信息中不是回复寻找本机，则给跳数加一和更新转发节点ip后转发该请求
        messageRREP.setHop((byte) (messageRREP.getHop() + 1));
        messageRREP.setRouteIP(ip);
        //转发
        forwardRREP(messageRREP);
    }

    //如果不是发送给自己的RREP则将其转发出去
    @Override
    public void forwardRREP(MessageRREP messageRREP) {
        System.out.println("节点" + ip + "转发节点"
                + messageRREP.getSrcIP() + "对节点" + messageRREP.getDestIP()
                + "的路由回复...");
        try {
            adhocTransfer.send(messageRREP);
            System.out.println("转发路由回复成功!");
        } catch (IOException e) {
            System.out.println("转发路由回复失败!");
        }
    }

    //在接收线程接收到数据后调用，主要解析数据类型，再恢复为相应的Message对象,并传递给相应的接收方法
    @Override
    public void dataParsing(byte[] bytes) {
        byte type = bytes[2];
        Message message = null;
        //如果是数据类型则恢复为数据MessageData，并且交给数据类型接收方法
        if (type == RouteProtocol.DATA) {
            message = MessageData.recoverMsg(bytes);
            receiveDATA((MessageData) message);
        }
        //如果是路由回复类型则恢复为数据MessageRREP，并且交给数据类型接收方法
        else if (type == RouteProtocol.RREP) {
            message = MessageRREP.recoverMsg(bytes);
            receiveRREP((MessageRREP) message);
        }
        //如果是路由请求类型则恢复为数据MessageRREQ，并且交给数据类型接收方法
        else if(type==RouteProtocol.RREQ){
            message = MessageRREQ.recoverMsg(bytes);
            receiveRREQ((MessageRREQ) message);
        }else if(type==RouteProtocol.HELLO){
            //交给处理hello报文的处理函数
            message = HelloMessage.recoverMsg(bytes);
            helloHandler(message);
        }else {
            System.out.println("无效数据格式!!");
        }
    }

    @Override
    public void sendMessage(String context,int destIP) {
        //节点想要给目的节点发送消息，首先查询本节点中路由表是否有可用的有效路由，如果没有就发起路由请求
        RouteEntry routeEntry = queryRouteTable(destIP);
        if (routeEntry == null) {
            System.out.println("本机还没有到节点"+destIP+"的路由，开始路由请求...");
            sendRREQ(destIP);
            //需要等待路由回复.....轮番查询五次，每次等待1秒，如果五次查询都失败，则宣布路由寻找失败
            try {
                for (int i = 1; i <= POLLING_COUNT; i++) {
                    System.out.println("等待回复："+i);
                    Thread.sleep(POLING_TIMER);
                    routeEntry = queryRouteTable(destIP);
                    if (routeEntry == null) {
                        if(i==POLLING_COUNT){
                            System.out.println("寻找路由失败！");
                            System.exit(1);
                        }
                        continue;
                    }else{
                        System.out.println("找到路由！");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //如果路由表中有可用路由则可以向其发送数据
        MessageData messageData = new MessageData();
        messageData.setDestIP(destIP);
        messageData.setSrcIP(ip);
        messageData.setNextIP(routeEntry.getNextHopIP());
        messageData.setContent(context.getBytes());
        try {
            adhocTransfer.send(messageData);
            System.out.println("数据发送成功!");
        } catch (IOException e) {
            System.out.println("数据发送失败!");
        }
    }

    @Override
    public void receiveDATA(MessageData messageData) {
        //收到数据类型的信息时不需要检查序列号
        int nextIP = messageData.getNextIP();
        if (nextIP != ip){
            System.out.println("本节点收到节点"+messageData.getSrcIP()+"发往节点"+messageData.getDestIP()
                    +"的数据！我不是中转节点，抛弃！");
            return;
        }

        int destIP = messageData.getDestIP();
        if (destIP == ip) {
            System.out.println("本节点收到来自" + messageData.getSrcIP()
                    + "的数据，" + "内容为：" + new String(messageData.getContent()));
            return;
        }

        System.out.println("本节点收到节点"+messageData.getSrcIP()+"发往节点"+messageData.getDestIP()
                +"的数据！我是中转节点，处理中！");

        //查询本节点的路由表，得到去往目的节点的下一跳节点，并改变消息中的下一跳节点地址后转发出去
        int next = queryRouteTable(destIP).getNextHopIP();
        messageData.setNextIP(next);
        forwardDATA(messageData);
    }

    /**
     * 这个几个forwardXXXX方法的日志记录完全可以用AOP实现，采用动态代理即可，但是劳资现在不想干！！！！
     *
     * @param messageData
     */
    @Override
    public void forwardDATA(MessageData messageData) {
        System.out.println("节点" + ip + "转发节点" + messageData.getSrcIP() + "对节点" + messageData.getDestIP()
                + "的数据...");
        try {
            adhocTransfer.send(messageData);
            System.out.println("转发数据成功!");
        } catch (IOException e) {
            System.out.println("转发数据失败!");
        }
    }

    @Override
    public RouteEntry queryRouteTable(int destIP) {
        RouteEntry routeEntry = routeTable.get(destIP);
        if (routeEntry != null && routeEntry.getState() == StateFlags.VALID)
            return routeEntry;
        return null;
    }

    //将接收到的hello报文的源节点IP加入到队列中
    public void helloHandler(Message message){
        int srdIP = message.getSrcIP();
        helloIP.add(srdIP);
    }



    //路由表维护函数，根据helloIP队列中的IP来维护路由表，在一定时间内没有收到某一节节点的hello报文，则将以该节点为下一中转节点的路由表
    //可用状态设置为不可用，并发送RRER
    public void maintainRouteTable(){
        Thread maintainRouteThread = new Thread(new Runnable() {
            //路由维护线程
            @Override
            public void run() {
                int ip1=0;
                Set<Integer> DestSet;
                Iterator<Integer> it;
                while(true){
                    if(helloIP.size()>0){
                        ip1 = helloIP.remove();
                        if(ip1!=0){
                                DestSet = getDestIPByNextIP(ip1);
                                it = DestSet.iterator();
                                while(it.hasNext()){
                                    routeTable.get(it.next()).setLifeTime(RouteEntry.MAX_LIFETIME);
                            }
                         }
                    }
                }
            }
        });

        //将维护线程设置为守护线程
        maintainRouteThread.setDaemon(true);

        maintainRouteThread.start();

    }

    //根据下一跳节点的ip获取目的节点的ip集合，根据该set查找route有效可用的表项
    private Set<Integer> getDestIPByNextIP(int ip){
        Set<Integer> sets = new HashSet<Integer>();
        Iterator<Integer> it = routeTable.keySet().iterator();
        RouteEntry routeEntry=null;
        int destIP;
        while(it.hasNext()){
            destIP=it.next();
            routeEntry=routeTable.get(destIP);
            if(routeEntry.getNextHopIP()==ip&&routeEntry.getState()==StateFlags.VALID)
            {
                sets.add(destIP);
            }
        }
        return sets;
    }
}
