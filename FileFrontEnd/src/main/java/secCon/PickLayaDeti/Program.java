package secCon.PickLayaDeti;

import secCon.PickLayaDeti.repository.JSONConfig;
import secCon.PickLayaDeti.server.Server;
import secCon.PickLayaDeti.thread.MulticastListener;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.utils.NetChooser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Program {

    public static final JSONConfig jsonConfig = new JSONConfig();

    // Différentes constantes
    private static String MULTICAST_ADDRESS;
    private static int MULTICAST_PORT;
    public static int UNICAST_PORT;
    public static String PATH;


    private static final byte[] BUFFER = new byte[1024];
    private MulticastSocket multicastSocket;
    private final NetworkInterface networkInterface;

    /**
     * Crée le constructeur de notre application afin de bien démarrer tous nos thread / serveurs.
     */
    public Program() {
        // Initialisation attributs
        extractInformationsFromJson();

        // Création du storManager
        StorManager storManager = new StorManager();

        // Démarrage du serveur pour le client.
        var server = new Server(storManager);
        System.out.printf("Démarrage du serveur sur le port %s\n", UNICAST_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Démarrage du multicast pour le SBE
        this.networkInterface = new NetChooser().getSelected();
        createMulticastSocket();

        // Le Multicast se rend disponible pour recevoir l'information du storeBackEnd.
        MulticastListener multicastListener = new MulticastListener(multicastSocket, BUFFER, storManager);
        Thread multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();
    }

    /**
     * Récupère les informations depuis notre fichier JSON.
     */
    private void extractInformationsFromJson() {
        MULTICAST_ADDRESS = jsonConfig.getMulticastAddress();
        MULTICAST_PORT = jsonConfig.getMulticastPort();
        UNICAST_PORT = jsonConfig.getUnicastPort();
        PATH = jsonConfig.getPath();
    }

    /**
    * Crée le socketMultiCast pour gérer la connexion à plusieurs SBE
     */
    private void createMulticastSocket() {
        try  {
            this.multicastSocket = new MulticastSocket(MULTICAST_PORT);
            this.multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));

            // Déclare l'interface pour pouvoir communiquer sur le réseau aux autres Multicast.
            multicastSocket.setInterface(networkInterface.getInetAddresses().nextElement());

        } catch (IOException ex) {
            System.out.println("Erreur lors de la réception du message du StoreBackEnd [" + ex.getMessage() + "].");
        }
    }



    public static void main(String[] args) {
        new Program();
    }

}
