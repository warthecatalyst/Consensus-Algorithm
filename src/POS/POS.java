package POS;

import OriginBlock.OriginBlockChain;
import POW.POWBlock;
import util.SHA256;

import java.util.Date;
import java.util.Random;

class POSConfig{
    final int MaxProbably = 255;
    final int MinProbably = 240;
    final int MinCoinAge = 5;   //ms
    final int MaxCoinAge = 30;  //ms
    final int Scale = 10;
}

public class POS extends Thread{

    public OriginBlockChain chain;
    public CoinPool coinPool;
    POSConfig config = new POSConfig();

    String TempAddress = "";
    public POS(String InetAddr){
        TempAddress = InetAddr;
    }

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
        super.run();
        chain = new POSBlockChain();
        chain.add(Genesis());
        System.out.println(chain.back());
        ProofOfStake();
    }

    public void ProofOfStake(){
        //在币池中的本地址具备的币龄计算
        int coinAge = 0;
        int realDif = config.MinProbably;
        long currentTime = new Date().getTime();

        //System.out.println("coinPool.size(): "+coinPool.size());

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
                coinPool.get(i).time = new Date();
            }
        }

        //根据币龄计算难度
        if (realDif + coinAge / config.Scale > config.MaxProbably){
            realDif = 255 - config.MaxProbably;
        }else{
            realDif = 255 - realDif - (int)coinAge / config.Scale;
        }

        System.out.print("realDif: "+realDif);

        //根据难度来计算
        for(int i = Integer.MIN_VALUE; ; i++){
            if(isValidNonce(chain.back().Hash,i,realDif)){
                String tmp = SHA256.getSHA256(chain.back().Hash+i);
                Random rand = new Random(new Date().getTime());
                int CoinNumber = rand.nextInt(3) + 2;
                Coin newOne = new Coin(new Date(),CoinNumber,TempAddress);
                POSBlock newblock = new POSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,tmp,realDif,i,newOne);
                System.out.println(newblock);
                chain.add(newblock);
                coinPool.add(newOne);
                break;
            }
            if(i==Integer.MAX_VALUE){
                break;
            }
            i++;
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
