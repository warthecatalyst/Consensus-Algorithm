package OriginBlock;

import Network.Client;
import Network.Server;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm extends Thread{
    public OriginBlockChain chain;
    public Server ServerThread;
    public List<Client> clients = new ArrayList<>();
    @Override
    public abstract void run();
}
