package OriginBlock;


import POW.POWBlockChain;
import Network.Client;
import Network.Server;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有算法类的祖先类，继承Thread，抽象重载run方法
 * 所有算法类都有一个区块链
 * 算法类包括连接向剩下3个Peer的Socket
 * 这样就不必在算法线程执行过程中通知Client线程执行，只需要发送即可
 */
public abstract class Algorithm extends Thread{
    protected final int PeerID;
    public OriginBlockChain chain;
    public Server serverThread;
    public List<Socket> clients;

    /**
     * Algorithm类的构造函数
     * @param PeerID 当前Peer的ID
     * @param socketList 存储发送给其他Peer的clientSockets
     * @param serverThread 存储Server线程
     */
    public Algorithm(int PeerID,List<Socket> socketList,Server serverThread){
        this.PeerID = PeerID;
        this.clients = socketList;
        this.serverThread = serverThread;
    }
    @Override
    public abstract void run();

    /**
     * 向其他的Peer发送挖到的矿
     * @param block
     * @return
     */
    protected abstract boolean sendToServers(OriginBlock block);
}
