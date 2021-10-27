package POW;

import java.util.Date;

/**
 * 用于创建创世区块的类
 */
public class GenesisBlock {
    public static Block genesisBlock(){
        Block block = new Block(0,new Date(),"","","",0,4);
        block.setHash(String.valueOf(block.hashCode()));
        return block;
    }
}
