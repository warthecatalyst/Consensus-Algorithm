package OriginBlock;

import java.util.Date;

/**
 * 定义抽象类OriginBlock，3个POX算法的区块都继承该类
 */

public abstract class OriginBlock implements java.io.Serializable{
    public final int Index;
    public final Date timeStamp;
    public final String Data;
    public final String Hash;
    public final String Prehash;

    public OriginBlock(int Index,Date timeStamp,String Data,String Prehash,String Hash){
        this.Index = Index;
        this.timeStamp = timeStamp;
        this.Data = Data;
        this.Prehash = Prehash;
        this.Hash = Hash;
    }


    public abstract boolean Verify();

    @Override
    public String toString() {
        return "OriginBlock{" +
                "Index=" + Index +
                ", timeStamp=" + timeStamp +
                ", Data='" + Data + '\'' +
                ", Hash='" + Hash + '\'' +
                ", Prehash='" + Prehash + '\'' +
                '}';
    }
}
