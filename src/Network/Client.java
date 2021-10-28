package Network;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * 每个Peer的客户类
 */
public class Client implements Runnable{
    private final String ServerAddr;
    private final int Serverport;

    public Client(String ServerAddr,int Serverport){
        this.ServerAddr = ServerAddr;
        this.Serverport = Serverport;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ServerAddr,Serverport);
            BufferedReader isFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter osToServer = new PrintWriter(socket.getOutputStream(),true);

            while(true){
                String msg = "to Server: Hello this is Client";
                osToServer.println(msg);
                String res = isFromServer.readLine();
                System.out.println("Message from Server:"+res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String ServerAddr;
        int ServerPort;

        System.out.println("请输入服务器的IP地址");
        ServerAddr = scanner.next();
        System.out.println("请输入服务器的端口号");
        ServerPort = scanner.nextInt();

        Client client = new Client(ServerAddr,ServerPort);
        client.run();
    }
}
