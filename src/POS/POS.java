package POS;

import Network.Server;
import OriginBlock.Algorithm;
import OriginBlock.OriginBlock;
import OriginBlock.OriginBlockChain;
import POW.POWBlock;
import util.SHA256;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.security.PublicKey;
import java.util.*;

class POSConfig{
    final int MaxProbably = 255;
    final int MinProbably = 250;
    final int MinCoinAge = 5;   //ms
    final int MaxCoinAge = 30;  //ms
    final int Scale = 10;
}

public class POS extends Algorithm {

    public CoinPool coinPool;
    POSConfig config = new POSConfig();
    String TempAddress = "";

    public POS(int PeerID, List<Socket> socketList,String InetAddr){
        super(PeerID,socketList);
        TempAddress = InetAddr;
    }

    public boolean JumpToNextRound = false;

    //创建创世区块
    public POSBlock Genesis(){
        //新建币池
        Random rand = new Random(new Date().getTime());
        int CoinNumber = rand.nextInt(3) + 2;
        Coin newCoin = new Coin(new Date(),CoinNumber,TempAddress);
        coinPool = new CoinPool();
        coinPool.add(newCoin);
        String tmp = SHA256.getSHA256(""+0);
        return new POSBlock(0,new Date(),"","",tmp,0,0,newCoin);
    }

    @Override
    public void run() {
        chain = new POSBlockChain();
        chain.add(Genesis());
        System.out.println(chain.back());

        while (true){
            ProofOfStake();
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

    public void ProofOfStake(){
        //在币池中的本地址具备的币龄计算
        int coinAge = 0;
        int realDif = config.MinProbably;
        long currentTime = new Date().getTime();

        //设置Dirty值，标注参与币龄计算的币
        ArrayList<Boolean> DirtyCoin = new ArrayList<>();
        for (int i = 0 ;i < coinPool.size(); i++){
            DirtyCoin.add(false);
        }

        float curCoinAge;
        for (int i = 0; i < coinPool.size(); i++){

            if (coinPool.get(i).time.getTime() + config.MinCoinAge < currentTime ){
                if (currentTime - coinPool.get(i).time.getTime() < config.MaxCoinAge){
                    curCoinAge = (currentTime - coinPool.get(i).time.getTime());
                    //curCoinAge = (float) Math.floor(curCoinAge / 10);   //每10ms算一次币龄
                }else{
                    curCoinAge = config.MaxCoinAge;
                }
                coinAge += coinPool.get(i).coinNumber * curCoinAge;

                System.out.println("coinAge:  "+coinAge);

                //参与挖矿的币龄置为0
                //coinPool.get(i).time = new Date();

                //参与计算的币被标注为dirty
                DirtyCoin.set(i,true);
            }
        }

        //根据币龄计算难度
        if (realDif + coinAge / config.Scale > config.MaxProbably){
            realDif = 260 - config.MaxProbably;
        }else{
            realDif = 260 - realDif - (int)coinAge / config.Scale;
        }

        System.out.print("realDif: "+realDif);

        //根据难度来计算
//        for(int i = Integer.MIN_VALUE; ; i++){
//
//            if (SuspendFlag == true){
//                return;
//            }
//
//            if(isValidNonce(chain.back().Hash,i,realDif)){
//                String tmp = SHA256.getSHA256(chain.back().Hash+i);
//                Random rand = new Random(new Date().getTime());
//                int CoinNumber = rand.nextInt(3) + 2;
//                Coin newOne = new Coin(new Date(),CoinNumber,TempAddress);
//                POSBlock newblock = new POSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,tmp,realDif,i,newOne);
//                System.out.println(newblock);
//                chain.add(newblock);
//                coinPool.add(newOne);
//
//                //发送
//                AwakeClients(newblock);
//                break;
//            }
//            if(i==Integer.MAX_VALUE){
//                break;
//            }
//            i++;
//        }

        //JumpToNextRound
        JumpToNextRound = false;
        //根据real难度来POW
        int random = (int) (Math.random()*12888);
        int i = random;
        while(true){
            //如果收到了其他节点的挖到矿的信息
            if (JumpToNextRound == true){
                JumpToNextRound = false;
                break;
            }

            if(isValidNonce(chain.back().Hash,i,realDif)){
                String tmp = SHA256.getSHA256(chain.back().Hash+i);
                Random rand = new Random(new Date().getTime());
                int CoinNumber = rand.nextInt(3) + 2;
                Coin newOne = new Coin(new Date(),CoinNumber,TempAddress);
                POSBlock newBlock = new POSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,tmp,realDif,i,newOne);
                System.out.println("挖到新区块:"+newBlock);
                chain.add(newBlock);
                coinPool.add(newOne);

                //将参与生成区块的币龄清空
                for(int j = 0; j < DirtyCoin.size();j++){
                    if (DirtyCoin.get(j) == true){
                        coinPool.get(j).time = new Date();
                    }
                }

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

    public void AwakeClients(POSBlock block){
        for (int i = 0; i< clients.size(); i++){
            System.out.println("send");
            sendToServers(block);
        }
    }

    private boolean isValidNonce(String Prehash,int nonce, int realDif){
        String cc = Prehash+nonce;
        //System.out.println("In isValidNonce, tmp = "+Prehash+nonce);
        String tmp = SHA256.getSHA256(Prehash+nonce);
        for(int i = 0;i<realDif;i++){
            if(tmp.charAt(i)!='0'){
                return false;
            }
        }
        //System.out.println("In isValidNonce, ans = "+tmp);
        return true;
    }
}
