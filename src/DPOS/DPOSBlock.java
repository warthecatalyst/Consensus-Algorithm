package DPOS;

import OriginBlock.OriginBlock;
import java.util.Date;

class Node{
    String AddressName;
    int voteNumber;
    int round;

    Node(){

    }

    Node(String Addr, int number,int round){
        AddressName = Addr;
        voteNumber = number;
        this.round = round;
    }

    @Override
    public boolean equals(Object obj) {
        return this.round == ((Node)obj).round && this.voteNumber == ((Node)obj).voteNumber && this.AddressName == ((Node)obj).AddressName;
    }
}

public class DPOSBlock extends OriginBlock {
    public Node blockNode;

    public DPOSBlock(int Index, Date timeStamp, String Data, String Prehash, String Hash, Node blockNode) {
        super(Index, timeStamp, Data, Prehash, Hash);
        this.blockNode = blockNode;
    }

    //在verify中进行发送出的内容是否为投票内容
    @Override
    public boolean Verify() {
        return true;
    }
}
