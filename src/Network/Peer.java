package Network;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * 实现P2P网络的实际类
 */
public class Peer implements Runnable{
    //peer的ID
    private static int PeerID;
    //peer的portnum
    private static int Portnum;
    //其他peer的地址
    private static List<String> addrs;
    //其他peer的portnum
    private static List<Integer> portnums;

    public Peer(int ID,int Portnum,List<String> addrs,List<Integer> portnums){
        PeerID = ID;
        Peer.Portnum = Portnum;
        Peer.addrs = addrs;
        Peer.portnums = portnums;
    }

    @Override
    public void run() {
        System.out.println("Peer Started");
        try {
            Socket[] sockets = new Socket[addrs.size()];
            for(int i=0;i<sockets.length;i++){
                sockets[i] = new Socket(addrs.get(i),portnums.get(i));
            }

            BufferedReader[] bufrs = new BufferedReader[sockets.length];
            for(int i=0;i<bufrs.length;i++){
                bufrs[i] = new BufferedReader(new InputStreamReader(sockets[i].getInputStream()));
            }
            PrintWriter[] prwrs = new PrintWriter[sockets.length];
            for(int i=0;i<prwrs.length;i++){
                prwrs[i] = new PrintWriter(sockets[i].getOutputStream(),true);
            }
            String msg = "Completed";
            while(true){
//                Thread.sleep(50);
//                for(int i=0;i<prwrs.length;i++){
//                    prwrs[i].println(msg);
//                }
                for(int i=0;i<bufrs.length;i++){
                    String res = bufrs[i].readLine();
                    System.out.println(res);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
