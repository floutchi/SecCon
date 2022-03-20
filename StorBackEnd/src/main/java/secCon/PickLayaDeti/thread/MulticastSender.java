package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;

import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MulticastSender implements Runnable{

    private final MulticastSocket multicastSocket;
        private boolean stop = false;

        public MulticastSender(MulticastSocket multicastSocket) {
            this.multicastSocket = multicastSocket;
            System.out.println("[MulticastSender] Setting interface");
        }

    /**
     * Envoie le message multicast au FFE sur l'adresse et le port multicast
     */
    @Override
        public void run() {
            String message = "HELLO " + Program.UNIQUE_ID + " " + Program.UNICAST_PORT;
            try {
                while(!stop) {
                    DatagramPacket dp = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),
                            message.length(),
                            InetAddress.getByName(Program.MULTICAST_ADDRESS),
                            Program.MULTICAST_PORT);

                    multicastSocket.send(dp);
                    System.out.println("[MulticastSender] Sending: " + message);
                    Thread.sleep(Program.MULTICAST_DELAY_IN_SECONDS * 1000L);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                multicastSocket.close();
            }
        }

        public void stop() {
            stop = true;
        }
}
