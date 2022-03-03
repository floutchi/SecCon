package secCon.PickLayaDeti;


import secCon.PickLayaDeti.thread.MulticastListener;
import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.utils.NetChooser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Program {
    private static final String DEFAULT_DESTINATION = "224.66.66.1";
    private static final int DEFAULT_PORT = 15201;

    private static byte[] buffer = new byte[1024];
    private final StorManager storManager;
    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;
    private secCon.PickLayaDeti.thread.StorProcessor client;

    public Program() {
        this.storManager = new StorManager();
        this.networkInterface = new NetChooser().getSelected();
        createMulticastSocket();

        // Le Multicast se rend disponible pour recevoir l'information du storeBackEnd.
        MulticastListener multicastListener = new MulticastListener(multicastSocket, buffer, this);
        Thread multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();
    }

    private void createMulticastSocket() {
        try  {
            this.multicastSocket = new MulticastSocket(DEFAULT_PORT);
            this.multicastSocket.joinGroup(InetAddress.getByName(DEFAULT_DESTINATION));

            // Déclare l'interface pour pouvoir communiquer sur le réseau aux autres Multicast.
            multicastSocket.setInterface(networkInterface.getInetAddresses().nextElement());

        } catch (IOException ex) {
            System.out.println("Erreur lors de la réception du message du StoreBackEnd [" + ex.getMessage() + "].");
        }
    }



    public static void main(String[] args) {
        new Program();
    }

    public void createClient(String[] informations) {
        System.out.printf("[Program] Receiving HELLO from %s with ID %s (unicast port: %s) \r\n", informations[2], informations[0], informations[1]);
        if (client != null) return;

        var process = new ServerInfo(informations[0],  informations[2], Integer.parseInt(informations[1]));
        this.client = new secCon.PickLayaDeti.thread.StorProcessor(process, this.storManager);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

}
