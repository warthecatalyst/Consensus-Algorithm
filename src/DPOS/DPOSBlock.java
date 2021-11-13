package DPOS;

import OriginBlock.OriginBlock;
import java.util.Date;

class Node{
    String AddressName;
    int voteNumber;

    Node(){

    }

    Node(String Addr, int number){
        AddressName = Addr;
        voteNumber = number;
    }
}

public class DPOSBlock extends OriginBlock {
    public Node blockNode;

    public DPOSBlock(int Index, Date timeStamp, String Data, String Prehash, String Hash, Node blockNode) {
        super(Index, timeStamp, Data, Prehash, Hash);
        this.blockNode = blockNode;
    }


    @Override
    public boolean Verify() {
        return false;
    }
}
