package POW;

import OriginBlock.OriginBlock;
import OriginBlock.OriginBlockChain;
import util.SHA256;

import java.util.Date;

/**
 * 运行POW算法的线程类，继承Thread覆盖run方法
 */
public class POW extends Thread{
    public static final int DIF = 4;    //在这个简单的POW系统中，不需要进行难度值的变化
    public OriginBlockChain chain;
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

        System.out.println(chain.back());

        //尝试挖矿过程的正确性
        for(int i = Integer.MIN_VALUE; ; i++){
            if(isValidNonce(chain.back().Hash,i)){
                String tmp = SHA256.getSHA256(chain.back().Hash+i);
                POWBlock newblock = new POWBlock(1,new Date(),"",chain.back().Hash,tmp,DIF,i);
                System.out.println(newblock);
                chain.add(newblock);
                break;
            }
            if(i==Integer.MAX_VALUE){
                break;
            }
            i++;
        }

    }

    private boolean isValidNonce(String Prehash,int nonce){
        String cc = Prehash+nonce;
        //System.out.println("In isValidNonce, tmp = "+Prehash+nonce);
        String tmp = SHA256.getSHA256(Prehash+nonce);
        for(int i = 0;i<DIF;i++){
            if(tmp.charAt(i)!='0'){
                return false;
            }
        }
        //System.out.println("In isValidNonce, ans = "+tmp);
        return true;
    }
}
