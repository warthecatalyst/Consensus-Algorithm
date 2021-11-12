package DPOS;

import OriginBlock.OriginBlock;

import java.util.Date;

public class DPOSBlock extends OriginBlock {
    public DPOSBlock(int Index, Date timeStamp, String Data, String Prehash, String Hash) {
        super(Index, timeStamp, Data, Prehash, Hash);
    }

    @Override
    public boolean Verify() {
        return false;
    }
}
