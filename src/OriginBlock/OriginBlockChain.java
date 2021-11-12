package OriginBlock;

import java.util.ArrayList;

/**
 * 原始的区块链类，构建一个区块链
 */
public class OriginBlockChain extends ArrayList<OriginBlock> {
    public OriginBlock back(){    //返回最后一个区块
        return get(this.size()-1);
    }
}
