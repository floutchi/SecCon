package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.ServerInfo;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.security.Hasher;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class StorProcessor implements Runnable {
    private final StorManager manager;

    ServerInfo process;
    private boolean stop;

    private boolean isBusy = false;

    Socket server;

    BufferedWriter out;
    BufferedReader in;

    Task t;

    private Hasher hasher;

    public StorProcessor(ServerInfo process, StorManager manager, Hasher hasher) {
        this.process = process;
        this.manager = manager;
        this.hasher = hasher;
    }

    public ServerInfo getServerInfo() {
        return process;
    }


    @Override
    public void run() {
       var ipAddress = process.getDomain();
       var port = process.getPort();

        // Affichage la demande de connexion
        System.out.printf("[StorProcessorRunnable][run] Attempting connection to %s:%d\r\n", ipAddress, port);

        try {
            server = new Socket("127.0.0.1", port);
            stop = false;
            // Déclare une sortie pour envoyer un message qui va vérifier la connexion.
            this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8));
            this.in = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));

            do {
                Thread.sleep(0);
                if(t != null) {
                    isBusy = true;
                    var hashedName = hasher.clearTextToHash(t.getFileName());

                    if(t.getProtocol().equals("SENDFILE")) {
                        out.write("SENDFILE " + hashedName + " " + t.getFileSize() + "\n");
                        out.flush();
                        System.out.println("[StorProcessor][sendMessage] sending 'SENDFILE'");
                        try {
                            FileSender fileSender = new FileSender(Program.PATH);
                            fileSender.sendFile(t.getFileName(), server.getOutputStream());

                            String line = in.readLine();
                            System.out.println("[StorProcessor][run] received : " + line);
                            manager.resultTask(line);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(t.getProtocol().equals("REMOVEFILE")) {
                        out.write("ERASEFILE " + t.getFileName() + "\n");
                        out.flush();
                        System.out.println("[StorProcessor][run] sending 'ERASEFILE'");

                        String line = in.readLine();
                        System.out.println("[StorProcessor][run] received : " + line);
                        manager.resultTask(line);
                    }

                    if(t.getProtocol().equals("GETFILE")) {
                        out.write("RETRIEVEFILE " + hashedName + "\n");

                        out.flush();
                        System.out.println("[StorProcessor][run] sending 'RETRIEVEFILE'");

                        String line = in.readLine();
                        System.out.println("[StorProcessor][run] received : " + line);
                        manager.resultTask(line);
                    }

                    isBusy = false;
                    this.t = null;
                }

            } while(!stop);

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setTask(Task t) {
        this.t = t;

    }


    private void stop() {
        this.stop = true;
    }

    public InputStream getInputStream() throws IOException {
        return server.getInputStream();
    }
}
