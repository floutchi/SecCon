package secCon.PickLayaDeti;


import secCon.PickLayaDeti.Server.Server;
import secCon.PickLayaDeti.Thread.ClientRunnable;
import secCon.PickLayaDeti.Thread.MulticastSender;
import secCon.PickLayaDeti.netChooser.NetChooser;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

public class Program implements AppController {

    public final static String GROUP = "224.66.66.1";
    public final static int PORT = 15201;
    public final static String DOMAIN = "picklayadeti.sbe22.1";

    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;
    private List<ClientRunnable> clients;

    public static void main(String[] args) {
        new Program();
    }

    public Program() {

        this.networkInterface = new NetChooser().getSelected();
        createMulticastSocket();

        MulticastSender multicastSender = new MulticastSender(multicastSocket, this);
        Thread multicastSenderThread = new Thread(multicastSender);
        multicastSenderThread.start();

        clients = new ArrayList<>();
        Server server = new Server(this);
        server.startListening();
    }

    private void createMulticastSocket() {
        try {
            this.multicastSocket = new MulticastSocket(PORT);
            this.multicastSocket.setInterface(networkInterface.getInetAddresses().nextElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastToAllClientsExceptMe(ClientRunnable clientRunnable, String line) {
        for(ClientRunnable current : clients) {
            if(current != clientRunnable) {
                current.sendMessage(line);
            }
        }
    }

    @Override
    public void registerClient(ClientRunnable clientRunnable) {

    }

    @Override
    public void unregisterClient(ClientRunnable clientRunnable) {

    }
}
