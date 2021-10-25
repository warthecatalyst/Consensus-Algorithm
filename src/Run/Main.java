package Run;

import POW.Block;
import POW.BlockChain;
import POW.GenesisBlock;

public class Main {
    public static void main(String[] args) {
        BlockChain blockChain = new BlockChain();
        Block genesisBlock = GenesisBlock.genesisBlock();
        blockChain.add(genesisBlock);
    }
}
