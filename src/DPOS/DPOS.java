package DPOS;

import Network.Server;
import OriginBlock.Algorithm;
import util.SHA256;

import java.io.IOException;
import java.util.*;

class Config{
    int round = 1;  //轮次
    final int delegateNumber = 2;   //每轮产生选举人数
}

public class DPOS extends Algorithm {

    Config dposConfig = new Config();

    //统计计票
    private List<Node> VotePool = new ArrayList<Node>();
    private int MyIndexThisRound = -1;
    private int Pointer = 0;

    //创建创世区块
    public static DPOSBlock Genesis(){
        String tmp = SHA256.getSHA256(""+0);
        return new DPOSBlock(0,new Date(),"","",tmp, new Node("",0,0));
    }

    @Override
    public void run() {
        chain = new DPOSBlockChain();
        chain.add(Genesis());
    }

    public DPOSBlock GenerateBlock(Node blockNode){
        String hash = SHA256.getSHA256(chain.back().Hash+(chain.back().Index + 1));
        DPOSBlock block = new DPOSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,"",blockNode);
        chain.add(block);
        return block;
    }

    public DPOSBlock Vote(int round){
        //简化，假设每个人的持币量均等，投票权重一致
        //随机获取一个ip
        List<String> ipAddress = new ArrayList<>();
        for (int i = 0;i < clients.size();i++){
            ipAddress.add(clients.get(i).ServerAddr);
        }
        ipAddress.add(ServerThread.Addr);
        Random rand = new Random(new Date().getTime());
        int randNumber = rand.nextInt(clients.size());
        Node vote = new Node(ipAddress.get(randNumber),1,round);
        //index为-1的区块为投票信息
        return new DPOSBlock(-1,new Date(),"","","",vote);
    }

    //send vote Message
    public void SendVoteBlock(){
        //产生此轮的投票信息
        DPOSBlock voteBlock = Vote(dposConfig.round);
        //加入此轮的投票池
        VoteManager(voteBlock.blockNode);

        for (int i = 0; i< clients.size(); i++){
            try {
                System.out.println("send Vote");
                clients.get(i).send(voteBlock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //send block
    public void SendGenerateBlock(Node node){

        for (int i = 0; i< clients.size(); i++){
            try {
                System.out.println("send");
                clients.get(i).send(GenerateBlock(node));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //投票池进行排序和产生
    public void VoteManager(Node newNode){
        if (dposConfig.round == newNode.round){
            VotePool.add(newNode);
            //如果收到了全部投票
            if (VotePool.size() == clients.size() + 1){
                Map<Node,Integer> voteCount = new TreeMap<Node, Integer>();
                for (Node node:VotePool) {
                    if (!voteCount.keySet().contains(node))
                        voteCount.put(node,node.voteNumber);
                    else
                        voteCount.replace(node, voteCount.get(node) + node.voteNumber) ;
                }

                //排序
                List<Map.Entry<Node,Integer>> list = new ArrayList<Map.Entry<Node,Integer>>(voteCount.entrySet());
                Collections.sort(list,new Comparator<Map.Entry<Node,Integer>>() {
                    @Override
                    public int compare(Map.Entry<Node, Integer> o1, Map.Entry<Node, Integer> o2) {
                        if (o1.getValue() < o2.getValue()){
                            return 1;
                        }else if(o1.getValue() == o2.getValue()){
                            return o1.getKey().AddressName.compareTo(o2.getKey().AddressName);
                        }else{
                            return -1;
                        }
                    }
                });

                VotePool.clear();
                //检查是否有自己
                for (int i = 0;i < dposConfig.delegateNumber;i++){
                    VotePool.add(list.get(i).getKey());
                    if (list.get(i).getKey().AddressName == ServerThread.Addr){
                        if (i == 0){
                            //由我先产生一个区块
                            SendGenerateBlock(list.get(i).getKey());
                            Pointer++;
                        }else{
                            MyIndexThisRound = i;
                        }
                    }
                }

            }
        }
    }

    public void VerifyBlock(DPOSBlock block){
        if (block.Index == -1){
            VoteManager(block.blockNode);
        }else{
            chain.add(block);
            Pointer++;
            if (MyIndexThisRound == Pointer){
                SendGenerateBlock(VotePool.get(Pointer));
            }
            if (Pointer == dposConfig.delegateNumber){
                dposConfig.round++;
                MyIndexThisRound = -1;
                Pointer = 0;
                VotePool.clear();

                SendVoteBlock();
            }
        }
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

/*
对于一个节点：
首先随机出一个节点的ID，进行投票节点信息的产生，并加入这一轮的计票池中
然后将节点信息发送出去
在接受到区块信息后，判断是否为投票信息

根据所在的轮次，统计有效投票，当投票数目达到n时，排序得到投票结果。投票的前两名发送区块。
在收到了两个区块后(current index)，round+1，开启下一轮的投票
 */