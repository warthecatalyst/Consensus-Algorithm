package DPOS;


import OriginBlock.Algorithm;
import OriginBlock.OriginBlock;
import OriginBlock.OriginBlockChain;
import util.SHA256;

import java.net.Socket;
import java.util.Date;
import java.util.List;

class Config{
    int round = 0;  //轮次
    final int delegateNumber = 2;   //每轮产生选举人数
}

public class DPOS extends Algorithm {
    public OriginBlockChain chain;
    Config dposConfig = new Config();

    public DPOS(int PeerID, List<Socket> socketList) {
        super(PeerID, socketList);
    }

    //创建创世区块
    public static DPOSBlock Genesis(){
        String tmp = SHA256.getSHA256(""+0);
        return new DPOSBlock(0,new Date(),"","",tmp, new Node("",0));
    }

    @Override
    public void run() {
        chain = new DPOSBlockChain();
        chain.add(Genesis());
    }

    @Override
    protected boolean sendToServers(OriginBlock block) {
        return false;
    }

    public DPOSBlock GenerateBlock(Node blockNode){
        String hash = SHA256.getSHA256(chain.back().Hash+(chain.back().Index + 1));
        return new DPOSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,"",new Node());

    }

    public Node Vote(int round){
        //简化，假设每个人的持币量均等，投票权重一致
        //随机获取一个ip


        return new Node("",1);
    }
}

/*
Thread:
GensisBlock
进入round1：
各节点投票，汇总投票结果，排序，取前两名，随机一个顺序，由这两个节点按照顺序依次产生区块，广播。
进入round2:
........

problem:
如何协同各节点投票并取得总的投票结果。

 */