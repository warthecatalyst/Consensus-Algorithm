package POW;

import java.util.Date;

/**
 * 区块类
 */
public class Block {
    public final int Index;  //区块高度
    public final Date timeStamp; //时间戳
    public final String Data;
    public String Hash;
    public final String Prehash;
    public final int Nonce;
    public final int Difficulty;

    public Block(int Index,Date timeStamp,String Data,String Hash,String PreHash,int Nonce,int Difficulty){
        this.Index = Index;
        this.timeStamp = timeStamp;
        this.Data = Data;
        this.Hash = Hash;
        this.Prehash = PreHash;
        this.Nonce = Nonce;
        this.Difficulty = Difficulty;
    }

    public void setHash(String hash){
        this.Hash = hash;
    }
}
