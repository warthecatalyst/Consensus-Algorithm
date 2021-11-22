package Run;

import DPOS.DPOS;
import Network.Server;
import OriginBlock.Algorithm;
import POS.POS;
import POW.POW;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static String InetAddr = "127.0.0.1";
    private static int Portnum = 9090;
    private static int PeerID;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入自己的IP地址：(默认为127.0.0.1)");
        String inet = scanner.nextLine();
        if(!inet.isEmpty()){
            InetAddr = inet.trim();
        }
        System.out.println("自己的IP地址为:"+InetAddr);
        System.out.println("请输入自己的端口号:(默认为9090)");
        inet = scanner.nextLine();
        if(!inet.isEmpty()){
            Portnum = Integer.parseInt(inet);
        }
        System.out.println("自己的端口号为:"+Portnum);
        System.out.println("请输入自己的peerID号:");
        PeerID = scanner.nextInt();
        System.out.println("自己的peerID号为:"+PeerID);
        Algorithm Consensus;
        while (true)
        {
            System.out.println("请选择需要运行的协议(1.POW,2.POS,3.DPOS)：");
            int index=scanner.nextInt();
            if(index==1)
            {
                System.out.println("运行POW协议");
                Consensus=new POW(PeerID,new ArrayList<>());
                break;
            }else if(index==2)
            {
                System.out.println("运行POS协议");
                Consensus=new POS(PeerID,new ArrayList<>(),InetAddr);
                break;
            }else if(index==3)
            {
                System.out.println("运行POS协议");
                Consensus=new DPOS(PeerID,new ArrayList<>(),InetAddr);
                break;
            }else {
                System.out.println("输入错误！");
            }
        }
        Server server = new Server(InetAddr,Portnum,PeerID,Consensus);
        server.start();

        System.out.println("请输入其他peer的peerID(0退出):");
        int otherPeerID = scanner.nextInt();
        while(otherPeerID!=0){
            System.out.println("请输入该Peer的地址:");
            String otherAddr = scanner.next();
            System.out.println("请输入该Peer的端口号:");
            int otherport = scanner.nextInt();
            Socket socket = null;
            try {
                socket = new Socket(otherAddr,otherport);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Consensus.clients.add(socket);
            try {
                assert socket != null;
                Consensus.oosList.add(new ObjectOutputStream(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("请输入其他peer的peerID(0退出):");
            otherPeerID = scanner.nextInt();
        }
        Consensus.start();
    }
}

/*
192.168.5.18
9091
2
 */