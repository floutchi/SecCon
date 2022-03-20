package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.tasks.EraseFileTask;
import secCon.PickLayaDeti.tasks.RetrieveFileTask;
import secCon.PickLayaDeti.tasks.SendFileTask;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Classe permettant de gérer le communication avec le FFE
 */
public class ClientHandler implements Runnable {

    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private BufferedWriter out;
    private BufferedReader in;
    private int state = 0;

    /**
     * Constructeur permetant d'instancier l'input et le output vers le client
     * @param client Socket de connexion au FFE
     */
    public ClientHandler(Socket client) {
        try {
            this.client = client;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Va attendre un message du FFE et va vérifier quelle tâche le SBE doit réaliser
     */
    @Override
    public void run() {
        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {
                    if(line.length() < 100) {
                        System.out.println("[ClientHandler][run] received : '" + line + "'");
                    }
                    TaskManager taskManager;
                    taskManager = new SendFileTask(this);
                    if(taskManager.check(line)) taskManager.execute(line);
                    taskManager = new EraseFileTask(this);
                    if(taskManager.check(line)) taskManager.execute(line);
                    taskManager = new RetrieveFileTask(this);
                    if(taskManager.check(line)) taskManager.execute(line);
                } else {
                    stop = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try { client.close(); } catch (IOException ex) {}
        }
    }

    /**
     * Permet de recevoir un fichier du FFE
     * @param fileName  Nom du fichier
     * @param size      Taille du fichier
     */
    public void receiveFile(String fileName, int size) {
        try {
            FileReceiver fileReceiver = new FileReceiver(Program.PATH);
            fileReceiver.receiveFile(client.getInputStream(), fileName, size);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage("SEND_ERROR");
        }
    }

    /**
     * Permet d'envoyer un fichier au FFE
     * @param fileName  Nom du fichier à recevoir
     */
    public void sendFile(String fileName) {
        FileSender fileSender = new FileSender(Program.PATH);
        try {
            fileSender.sendFile(fileName, client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage("RETRIEVE_ERROR");
        }
    }


    /**
     * Permet d'envoyer un message au FFE
     * @param message
     */
    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            System.out.println("[ClientHandler][sendMessage] send : '" + message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

