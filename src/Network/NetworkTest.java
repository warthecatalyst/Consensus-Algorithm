package Network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

public class NetworkTest {
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

        Runnable server = new Server(InetAddr,Portnum,PeerID);
        server.run();
        List<Runnable> tasks = new ArrayList<>();
        System.out.println("请输入其他peer的peerID(0退出):");
        int otherPeerID = scanner.nextInt();
        while(otherPeerID!=0){
            System.out.println("请输入该Peer的地址:");
            String otherAddr = scanner.next();
            System.out.println("请输入该Peer的端口号:");
            int otherport = scanner.nextInt();
            tasks.add(new Client(otherAddr,otherport,PeerID));
            System.out.println("请输入其他peer的peerID(0退出):");
            otherPeerID = scanner.nextInt();
        }
        for(Runnable task:tasks){
            task.run();
        }
    }
}
