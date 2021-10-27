package Run;

import Network.Peer;
import POW.Block;
import POW.BlockChain;
import POW.GenesisBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        BlockChain blockChain = new BlockChain();
//        Block genesisBlock = GenesisBlock.genesisBlock();
//        blockChain.add(genesisBlock);

        //基本网络配置，此处的内容都根据自己的情况进行修改
        int ID = 1; //should be changed
        String initaddr = "10.22.37.222";
        int portnum = 9090;

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入其他设备的ID(0退出)");
        int inp = scanner.nextInt();
        List<String> peeraddrs = new ArrayList<>();
        List<Integer> peerPorts = new ArrayList<>();
        while(inp!=0){
            System.out.println("请输入其他设备的网络地址");
            String addr = scanner.next();
            System.out.println("请输入其他设备的端口号");
            int port = scanner.nextInt();
            peeraddrs.add(addr);
            peerPorts.add(port);
            System.out.println("请输入其他设备的ID(0退出)");
            inp = scanner.nextInt();
        }
        Peer peer = new Peer(ID,portnum,peeraddrs,peerPorts);
        peer.run();
        
    }

}
