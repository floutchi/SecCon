package secCon.PickLayaDeti.Thread;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.Program;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastSender implements Runnable {

    private final MulticastSocket multicastSocket;
    private AppController controller;
    private boolean stop = false;

    public MulticastSender(MulticastSocket multicastSocket, AppController controller) {
        this.multicastSocket = multicastSocket;
        this.controller = controller;
        System.out.println("[MulticastSender] Setting interface");
    }

    @Override
    public void run() {
        String message = "HELLO " + Program.DOMAIN + " " + Program.PORT;
        try {
            while(!stop) {
                DatagramPacket dp = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),
                        message.length(),
                        InetAddress.getByName(Program.GROUP),
                        Program.PORT);

                multicastSocket.send(dp);
                System.out.println("[MulticastSender] Sending: " + message);
                Thread.sleep(30000);
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
