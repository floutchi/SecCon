package secCon.PickLayaDeti.Thread;

import secCon.PickLayaDeti.Program;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastListener implements Runnable{

    private final DatagramSocket receiveStoreBackEnd;;
    private final byte[] buffer;
    private final Program program;
    private boolean stop = false;

    public MulticastListener(DatagramSocket receiveStoreBackEnd, byte[] buffer, Program program) {
        this.receiveStoreBackEnd = receiveStoreBackEnd;
        this.buffer = buffer;
        this.program = program;
        System.out.println("[MulticastListener] Setting interface");
    }



    @Override
    public void run() {
        System.out.println("= [MulticastListener] Started");

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
                System.out.println("> [MulticastListener] " + received);
                program.createClient(getInformations(received, buffered.getAddress()));

            }

        } catch (IOException e) {
            System.out.println("Erreur lors de la réception du Multicast : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retourne un tableau avec les informations demandées.
     * Le nom de domain.
     * Le port.
     * @return
     */
    public String[] getInformations(String received, InetAddress address) {
        var splittedArray = received.split(" ");
        return new String[]{splittedArray[1], splittedArray[2], address.toString().substring(1)};
    }

    public void stop() {
        stop = true;
    }
}
