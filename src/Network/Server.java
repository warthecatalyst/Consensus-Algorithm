package Network;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * 对于每一个Peer，都有一个服务器类和一个接收类
 */
public class Server implements Runnable{
    private final String Addr;
    private final int Portnum;
    private final int PeerID;
    public Server(String Addr,int Portnum,int peerID){
        this.Addr = Addr;
        this.Portnum = Portnum;
        this.PeerID = peerID;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Portnum,3,InetAddress.getByName(Addr));
            Socket socket = serverSocket.accept();
            BufferedReader isFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter osToClient = new PrintWriter(socket.getOutputStream(),true);
            while(true){
                String res = isFromClient.readLine();
                System.out.println("result get from client:"+res);
                String msg = "to client:hello this is Server "+ PeerID;
                osToClient.println(msg);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        //Server server = new Server(Addr,Portnum);
        //server.run();
    }
}
