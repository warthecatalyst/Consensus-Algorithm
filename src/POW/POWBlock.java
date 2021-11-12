package POW;

import OriginBlock.OriginBlock;
import util.SHA256;

import java.util.Date;

public class POWBlock extends OriginBlock {
    public final int difficulty;
    public final int nonce;

    public POWBlock(int Index, Date timeStamp, String Data, String Prehash,String Hash,int difficulty,int nonce){
        super(Index,timeStamp,Data,Prehash,Hash);
        this.difficulty = difficulty;
        this.nonce = nonce;
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

    @Override
    public String toString() {
        return "POWBlock{" +
                "difficulty=" + difficulty +
                ", nonce=" + nonce +
                ", Index=" + Index +
                ", timeStamp=" + timeStamp +
                ", Data='" + Data + '\'' +
                ", Hash='" + Hash + '\'' +
                ", Prehash='" + Prehash + '\'' +
                '}';
    }
}
