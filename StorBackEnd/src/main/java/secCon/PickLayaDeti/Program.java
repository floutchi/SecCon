package secCon.PickLayaDeti;


import secCon.PickLayaDeti.Thread.MulticastSender;
import secCon.PickLayaDeti.utils.NetChooser;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Program implements AppController {

    public final static String GROUP = "224.66.66.1";
    public final static int PORT = 15201;
    public final static String DOMAIN = "picklayadeti.sbe22.1";

    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;

    public static void main(String[] args) {
        new Program();
    }

    public Program() {
        this.networkInterface = new NetChooser().getSelected();
        createMulticastSocket();

        MulticastSender multicastSender = new MulticastSender(multicastSocket, this);
        Thread multicastSenderThread = new Thread(multicastSender);
        multicastSenderThread.start();
    }

    private void createMulticastSocket() {
        try {
            this.multicastSocket = new MulticastSocket(PORT);
            this.multicastSocket.setInterface(networkInterface.getInetAddresses().nextElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
