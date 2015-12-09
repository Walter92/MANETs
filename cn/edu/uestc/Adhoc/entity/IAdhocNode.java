package cn.edu.uestc.Adhoc.entity;

public interface IAdhocNode {
	//路由请求，当要给某一个节点发送数据时没有到该节点的路由时广播路由请求
	public static final int RREQ = 0xE1;
	//路由回应，当找到路由节点时回复寻找节点的节点
	public static final int RREP = 0xE3;
	//路由错误，没有找到路由，可能是节点中没有要找的节点
	public static final int RRER = 0xE5;
	//数据发送，表示该帧是一串数据，不是任何控制信息，按照路由途径发送即可
	public static final int DATA = 0xE6;
    //发送路由请求RRRQ
	void sendRREQ(String destIP);
    //接收路由请求RREQ
	void receiveRREQ(Message message);
    //转发路由请求RREQ
    void forwardRREQ(Message message);
    //回复路由响应RREP
    void sendRREP(Message message);
    //接收路由响应RREP
    void receiveRREP(Message message);
    //转发路由响应RREP
    void forwardRREP(Message message);
    //信息分发，当收到一个数据帧时判断该数据帧的类型然后在交给对应的处理程序
    void dispatch(Message message);
}
