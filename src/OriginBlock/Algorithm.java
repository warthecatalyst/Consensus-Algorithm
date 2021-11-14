package OriginBlock;

import Network.Client;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm extends Thread{
    public OriginBlockChain chain;
    public List<Client> clients = new ArrayList<>();
    @Override
    public abstract void run();
}
