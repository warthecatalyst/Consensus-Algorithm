package Network;

import DPOS.DPOS;
import DPOS.DPOSBlock;
import OriginBlock.OriginBlock;
import OriginBlock.Algorithm;
import POS.POS;
import POS.POSBlock;
import POW.POW;
import POW.POWBlock;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * 对于每一个Peer，都有一个服务器类和一个接收类
 */
public class Server extends Thread{
    public final String Addr;
    private final int Portnum;
    private final int PeerID;
    public final Algorithm poxThread;   //在server线程中启动挖矿线程
    ObjectInputStream isFromClient;

    public Server(String Addr,int Portnum,int peerID,Algorithm poxThread){
        this.Addr = Addr;
        this.Portnum = Portnum;
        this.PeerID = peerID;
        this.poxThread = poxThread;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Portnum,3,InetAddress.getByName(Addr));
            Socket socket = serverSocket.accept();
            //isFromClient = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            while(true){
                isFromClient = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                Object obj = isFromClient.readObject();
                System.out.println("read from other peer");
                synchronized (poxThread){
                    poxThread.yield();
                    OnReceive(obj);
                    poxThread.notify();
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void OnReceive(Object object) throws InterruptedException {
        if(object instanceof OriginBlock){
            if(((OriginBlock) object).Verify()){
                if (object instanceof DPOSBlock){
                    DPOSBlock dposBlock = (DPOSBlock) object;
                    if(DPOS.isVote(dposBlock)){
                        ((DPOS) poxThread).VotePool.add(dposBlock.blockNode);
                    }else{
                        poxThread.chain.add(dposBlock);
                        ((DPOS) poxThread).Pointer++;
                    }
                }else if(object instanceof POWBlock){
                    poxThread.chain.add((POWBlock) object);
                }else if(object instanceof POSBlock){
                    ((POS)poxThread).JumpToNextRound = true;
                    poxThread.chain.add((POSBlock) object);
                    //Thread.sleep(5);
                    //((POS)poxThread).SuspendFlag = false;
                }
                System.out.println("Block added to chain:\n"+poxThread.chain.back());
            }
        }else{
            System.err.println("Something goes wrong in OnReceive()----Wrong Type!");
        }
    }

    //设置一下一个Suspend的参数，然后根据悬停的情况来中止当前挖矿的执行
    /**
     * test Server main
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String Addr;
        int Portnum;
        System.out.println("请输入自己的IP地址");
        Addr = scanner.next();
        System.out.println("请输入自己的端口号");
        Portnum = scanner.nextInt();

        //Server server = new Server(Addr,Portnum,1);
        //server.start();
    }
}
