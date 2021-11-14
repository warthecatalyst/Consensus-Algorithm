package Network;

import OriginBlock.OriginBlock;
import OriginBlock.Algorithm;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * 每个Peer的客户类
 */
public class Client extends Thread{
    private final String ServerAddr;
    private final int Serverport;
    private final int peerID;
    private ObjectOutputStream osToServer;
    private Algorithm algorithm;
    public Client(String ServerAddr,int Serverport,int peerID){
        this.ServerAddr = ServerAddr;
        this.Serverport = Serverport;
        this.peerID = peerID;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ServerAddr,Serverport);
            BufferedReader isFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            osToServer = new ObjectOutputStream(socket.getOutputStream());

            while(true){
//                String msg = "to Server: Hello this is Client "+ peerID;
//                //osToServer.println(msg);
//                String res = isFromServer.readLine();
//                System.out.println("Message from Server:"+res);
//
//                //挖到矿之后调用send()方法
//
//                System.out.println("AWAKE");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void send(OriginBlock block) throws IOException {
        osToServer.writeObject(block);

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String ServerAddr;
        int ServerPort;

        System.out.println("请输入服务器的IP地址");
        ServerAddr = scanner.next();
        System.out.println("请输入服务器的端口号");
        ServerPort = scanner.nextInt();

        Client client = new Client(ServerAddr,ServerPort,1);
        client.start();
    }
}

