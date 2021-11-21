package POW;

import Network.Client;
import Network.Server;
import OriginBlock.OriginBlock;
import OriginBlock.OriginBlockChain;
import OriginBlock.Algorithm;
import util.SHA256;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 运行POW算法的线程类，继承Thread覆盖run方法
 */
public class POW extends Algorithm {
    public static final int DIF = 4;    //在这个简单的POW系统中，不需要进行难度值的变化
    public POW(int PeerID, List<Socket> socketList){
        super(PeerID,socketList);
    }

    //创建创世区块
    public static POWBlock Genesis(){
        String tmp = SHA256.getSHA256(""+0);
        return new POWBlock(0,new Date(),"","",tmp,DIF,0);
    }

    @Override
    public void run() {
        //先创造一个区块链和创世区块
        chain = new POWBlockChain();
        chain.add(Genesis());
        System.out.println("创世区块构建完毕：");
        System.out.println(chain.back());

        //等待10s让其他的Peer进来
        System.out.println("waiting for other peers to join:");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //开始挖矿
        while (true){
            ProofOfWork();
        }
    }

    @Override
    protected boolean sendToServers(OriginBlock block) {
        int i = 0;
        System.out.println("in sendToServers");
        for(ObjectOutputStream oos:oosList){
            System.out.println("Send to Server:"+clients.get(i++));
            try {
                oos.writeObject(block);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean isValidNonce(String Prehash, int nonce){
        String tmp = SHA256.getSHA256(Prehash+nonce);
        for(int i = 0;i<DIF;i++){
            if(tmp.charAt(i)!='0'){
                return false;
            }
        }
        return true;
    }

    private void ProofOfWork(){
        int random = (int) (Math.random()*12888);
        int i = random;
        while(true){
            if(isValidNonce(chain.back().Hash,i)){
                String tmp = SHA256.getSHA256(chain.back().Hash+i);
                POWBlock newBlock = new POWBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,tmp,DIF,i);
                System.out.println("挖到新区块:"+newBlock);
                chain.add(newBlock);
                //挖到矿发送给其他的Servers
                if(sendToServers(newBlock)){
                    break;
                }
            }
            if(i==Integer.MAX_VALUE){
                i = Integer.MIN_VALUE;
            }else{
                i++;
            }
            if(i==random){
                break;
            }
        }
    }
}
