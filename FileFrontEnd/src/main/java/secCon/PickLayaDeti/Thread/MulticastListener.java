package secCon.PickLayaDeti.Thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MulticastListener implements Runnable{

    private final DatagramSocket receiveStoreBackEnd;;
    private final byte[] buffer;
    private boolean stop = false;

    public MulticastListener(DatagramSocket receiveStoreBackEnd, byte[] buffer) {
        this.receiveStoreBackEnd = receiveStoreBackEnd;
        this.buffer = buffer;
        System.out.println("[MulticastSender] Setting interface");
    }



    @Override
    public void run() {
        System.out.println("[MulticastSender] Started");

        // Déclaration du buffer
        var buffered = new DatagramPacket(buffer, 1024);

        // Le Multicast se rend disponible pour recevoir l'information du storeBackEnd.
        try {
            while(!stop) {
                receiveStoreBackEnd.receive(buffered);
                String received = new String(buffered.getData(), 0, buffered.getLength());

                System.out.println("[MulticastListener] " + received);
                //TODO : une fois le message reçu, effectuer connexion TCP
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
    }
}
