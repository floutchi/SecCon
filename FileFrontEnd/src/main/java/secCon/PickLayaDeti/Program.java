package secCon.PickLayaDeti;

import secCon.PickLayaDeti.Thread.MulticastListener;
import secCon.PickLayaDeti.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Program {
    private static final String DEFAULT_DESTINATION = "224.66.66.1";
    private static final int DEFAULT_PORT = 15201;

    private static byte[] buffer = new byte[1024];
    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;
    private Client client;

    public Program() {
        this.networkInterface = new NetChooser().getSelected();
        client = null;
        createMulticastSocket();

        // Le Multicast se rend disponible pour recevoir l'information du storeBackEnd.
        MulticastListener multicastListener = new MulticastListener(multicastSocket, buffer, this);
        Thread multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();

        //var client = new Client();
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

    public void setInformations(String[] informations) {
        System.out.printf("Receiving HELLO from %s with ID %s (unicast port: %s) \r\n", informations[2], informations[0], informations[1]);
        if (client != null) return;
        this.client = new Client(informations[2], Integer.parseInt(informations[1]));
    }
}
