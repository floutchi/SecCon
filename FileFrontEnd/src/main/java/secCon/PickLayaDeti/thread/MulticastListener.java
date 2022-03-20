package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.tasks.sbe.HelloTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Déclare notre multicastListener afin de se connecter à plusieurs SBE.
 */
public class MulticastListener implements Runnable{

    private final DatagramSocket receiveStoreBackEnd;;
    private final byte[] buffer;
    private final StorManager manager;
    private boolean stop = false;

    /**
     * Déclare le constructeur de notre multicastlistener.
     * @param receiveStoreBackEnd le BackEnd avec le quel se connecté.
     * @param buffer notre buffer d'entrée.
     * @param manager afin d'ajouter nos processors.
     */
    public MulticastListener(DatagramSocket receiveStoreBackEnd, byte[] buffer, StorManager manager) {
        this.receiveStoreBackEnd = receiveStoreBackEnd;
        this.buffer = buffer;
        this.manager = manager;
        System.out.println("[MulticastListener] Setting interface");
    }



    @Override
    public void run() {
        System.out.println("[MulticastListener] Started");

        // Déclaration du buffer
        var buffered = new DatagramPacket(buffer, 1024);

        // Le Multicast se rend disponible pour recevoir l'information du storeBackEnd.
        try {
            while(!stop) {
                // Récupère le message.
                receiveStoreBackEnd.receive(buffered);
                // Note sous forme de string.
                String received = new String(buffered.getData(), 0, buffered.getLength());

                // Réceptionne et écrit le message reçu par le multicast.
                System.out.println("[MulticastListener] " + received);
                receiveMessage(received);
                //program.createClient(getInformations(received, buffered.getAddress()));
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la réception du Multicast : " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Récupère notre message multiCast afin d'exécuté la tâche demandée.
     * @param message le message à vérifier.
     */
    private void receiveMessage(String message) {
        var hello = new HelloTask(manager);
        if (hello.check(message)) {
            hello.execute(message);
        }
    }

    public void stop() {
        stop = true;
    }
}
