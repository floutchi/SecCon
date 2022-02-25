package secCon.PickLayaDeti;


import secCon.PickLayaDeti.Server.Server;
import secCon.PickLayaDeti.Thread.ClientRunnable;
import secCon.PickLayaDeti.Thread.MulticastSender;
import secCon.PickLayaDeti.repository.JSONConfig;
import secCon.PickLayaDeti.utils.NetChooser;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

public class Program implements AppController {

    private final JSONConfig jsonConfig = new JSONConfig();


    public static String UNIQUE_ID = "";
    public static String PATH = "";
    public static String MULTICAST_ADDRESS = "";
    public static int MULTICAST_PORT = 0;
    public static int MULTICAST_DELAY_IN_SECONDS = 0;
    public static int UNICAST_PORT = 0;

    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;
    private List<ClientRunnable> clients;

    public static void main(String[] args) {
        new Program();
    }


    public Program() {
        UNIQUE_ID = jsonConfig.getUniqueID();
        PATH = jsonConfig.getPath();
        MULTICAST_ADDRESS = jsonConfig.getMulticastAddress();
        MULTICAST_PORT = jsonConfig.getMulticastPort();
        MULTICAST_DELAY_IN_SECONDS = jsonConfig.getMulticastDelayInSeconds();
        UNICAST_PORT = jsonConfig.getUnicastPort();

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
            this.multicastSocket = new MulticastSocket(MULTICAST_PORT);
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
