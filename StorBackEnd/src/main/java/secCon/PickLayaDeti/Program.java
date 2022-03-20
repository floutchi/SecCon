package secCon.PickLayaDeti;

import secCon.PickLayaDeti.repository.JSONConfig;
import secCon.PickLayaDeti.server.Server;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.MulticastSender;
import secCon.PickLayaDeti.utils.NetChooser;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

public class Program {

    private final JSONConfig jsonConfig = new JSONConfig();


    public static String UNIQUE_ID = "";
    public static String PATH = "";
    public static String MULTICAST_ADDRESS = "";
    public static int MULTICAST_PORT = 0;
    public static int MULTICAST_DELAY_IN_SECONDS = 0;
    public static int UNICAST_PORT = 0;

    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;
    private List<ClientHandler> clients;

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

        MulticastSender multicastSender = new MulticastSender(multicastSocket);
        Thread multicastSenderThread = new Thread(multicastSender);
        multicastSenderThread.start();

        clients = new ArrayList<>();
        Server server = new Server();
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
}
