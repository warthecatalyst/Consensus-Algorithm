package POW;

import java.util.Date;

/**
 * 区块类
 */
public class Block {
    public final int Index;  //区块高度
    public final Date timeStamp; //时间戳
    public final String Data;   //当前块的数据
    public String Hash;         //当前块的Hash值
    public final String Prehash;    //前一个块的Hash值
    public final int Nonce;     //随机数
    public final int Difficulty;    //难度值

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
