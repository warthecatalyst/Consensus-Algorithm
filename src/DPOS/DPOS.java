package DPOS;

import Network.Server;
import OriginBlock.Algorithm;
import OriginBlock.OriginBlock;
import OriginBlock.OriginBlockChain;
import util.SHA256;

import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import util.SHA256;
import java.io.IOException;
import java.util.List;

class Config{
    int round = 1;  //轮次
    final int delegateNumber = 2;   //每轮产生选举人数
}

public class DPOS extends Algorithm {

    Config dposConfig = new Config();
    String InetAddr = "";
    //统计计票
    public List<Node> VotePool = new ArrayList<Node>();
    private int MyIndexThisRound = -1;
    public int Pointer = 0;

    private boolean finished = false;

    public DPOS(int PeerID, List<Socket> socketList,String Address) {
        super(PeerID, socketList);
        this.InetAddr = Address;
    }

    //创建创世区块
    public static DPOSBlock Genesis(){
        String tmp = SHA256.getSHA256(""+0);
        return new DPOSBlock(0,new Date(),"","",tmp, new Node("",0,0));
    }

    //收到的区块是不是投票信息
    public static boolean isVote(DPOSBlock dposBlock){
        return dposBlock.Index == -1;
    }

    @Override
    public void run() {
        chain = new DPOSBlockChain();
        chain.add(Genesis());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //开始投票
        SendVoteBlock();

        while (true){

            if(VotePool.size()==clients.size()+1 && !finished){
                //执行投票统计逻辑
                VoteManager();
            }
            Verify();

            //System.out.println("Whiling");
        }
    }

    @Override
    protected boolean sendToServers(OriginBlock block) {
        int i = 0;
        System.out.println("in sendToServers");
        for(ObjectOutputStream oos:oosList){
            System.out.println("Send to Server:"+clients.get(i++));
            try {
                oos.writeObject(block);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public DPOSBlock GenerateBlock(Node blockNode){
        String hash = SHA256.getSHA256(chain.back().Hash+(chain.back().Index + 1));
        DPOSBlock block = new DPOSBlock(chain.back().Index + 1,new Date(),"",chain.back().Hash,"",blockNode);
        chain.add(block);
        System.out.println("Generate block: "+block);
        return block;
    }

    public DPOSBlock Vote(int round){
        //简化，假设每个人的持币量均等，投票权重一致
        //随机获取一个ip
        List<String> ipAddress = new ArrayList<>();
        for (int i = 0;i < clients.size();i++){
            if (clients.get(i).getInetAddress().toString().charAt(0) == '/'){
                String addr = clients.get(i).getInetAddress().toString().substring(1);
                ipAddress.add(addr);
            }else{
                ipAddress.add(clients.get(i).getInetAddress().toString());
            }
        }
        ipAddress.add(InetAddr);
        Random rand = new Random(new Date().getTime());
        int randNumber = rand.nextInt(clients.size() + 1);
        Node vote = new Node(ipAddress.get(randNumber),1,round);
        //index为-1的区块为投票信息
        return new DPOSBlock(-1,new Date(),"","","",vote);
    }

    //send vote Message
    public void SendVoteBlock(){
        //产生此轮的投票信息
        DPOSBlock voteBlock = Vote(dposConfig.round);
        System.out.println("Send AddressName:"+voteBlock.blockNode.AddressName);
        //加入此轮的投票池
        VotePool.add(voteBlock.blockNode);
        sendToServers(voteBlock);
    }

    //send block
    public void SendGenerateBlock(Node node){
        sendToServers(GenerateBlock(node));
    }

    //投票池进行排序和产生
    public void VoteManager(){
        Map<Node,Integer> voteCount = new TreeMap<Node, Integer>();
        for (Node node:VotePool) {
            if (!voteCount.containsKey(node))
                voteCount.put(node,node.voteNumber);
            else
                voteCount.replace(node, voteCount.get(node) + node.voteNumber) ;
        }

        {
            //增加空票来解决获得投票人过少的情况
            for (int i = 0;i < clients.size();i++){
                if (clients.get(i).getInetAddress().toString().charAt(0) == '/'){
                    String addr = clients.get(i).getInetAddress().toString().substring(1);
                    voteCount.put(new Node(addr,1,dposConfig.round),0);
                }else{
                    voteCount.put(new Node(clients.get(i).getInetAddress().toString(),1,dposConfig.round),0);
                }
            }
            voteCount.put(new Node(InetAddr,1,dposConfig.round),0);
        }


        //排序
        List<Map.Entry<Node,Integer>> list = new ArrayList<Map.Entry<Node,Integer>>(voteCount.entrySet());
        list.sort(new Comparator<Map.Entry<Node, Integer>>() {
            @Override
            public int compare(Map.Entry<Node, Integer> o1, Map.Entry<Node, Integer> o2) {
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                } else if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().AddressName.compareTo(o2.getKey().AddressName);
                } else {
                    return -1;
                }
            }
        });


        VotePool.clear();
        //检查是否有自己
        int LoopNumber = Math.min(dposConfig.delegateNumber, list.size());
        for (int i = 0;i < LoopNumber;i++){
            VotePool.add(list.get(i).getKey());
            if (list.get(i).getKey().AddressName.equals(InetAddr)){
                if (i == 0){
                    //由我先产生一个区块
                    System.out.println("I send A block" + list.get(i).getKey());
                    SendGenerateBlock(list.get(i).getKey());
                    Pointer++;
                }else{
                    MyIndexThisRound = i;
                }
            }
        }
        finished = true;
    }

    private void Verify(){
        if (MyIndexThisRound == Pointer){
            SendGenerateBlock(VotePool.get(Pointer));
            Pointer++;
        }
        if (Pointer == dposConfig.delegateNumber){
            dposConfig.round++;
            MyIndexThisRound = -1;
            Pointer = 0;
            VotePool.clear();
            finished = false;
            SendVoteBlock();
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