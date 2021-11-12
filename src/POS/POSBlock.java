package POS;

import OriginBlock.OriginBlock;
import util.SHA256;

import java.util.ArrayList;
import java.util.Date;

class Coin{
    Date time;
    int coinNumber;
    String Address;

    Coin(){

    }

    Coin(Date now,int Number,String hostAdress){
        time = now;
        coinNumber = Number;
        Address = hostAdress;
    }

}

class CoinPool extends ArrayList<Coin> {

}

public class POSBlock extends OriginBlock {
    public int difficulty;
    public int nonce;
    public Coin coin;

    public POSBlock(int Index, Date timeStamp, String Data, String Prehash, String Hash, int difficulty,int nounce,Coin newCoin) {
        super(Index, timeStamp, Data, Prehash, Hash);
        this.difficulty = difficulty;
        this.nonce = nounce;
        this.coin = newCoin;
    }

    @Override
    public boolean Verify() {
        for(int i=0;i<difficulty;i++){
            if(Hash.charAt(i)!='0'){
                return false;
            }
        }
        String tmp = Prehash + nonce;
        String cur = SHA256.getSHA256(tmp);
        System.out.println("Verifying SHA256 Hash:"+cur);
        System.out.println("Current SHA256 Hash:"+Hash);
        return cur.equals(Hash);
    }
}
