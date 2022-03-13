package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.tasks.EraseFileTask;
import secCon.PickLayaDeti.tasks.SendFileTask;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Socket client;
    private AppController controller;
    private boolean stop = false;
    private boolean connected = false;
    private BufferedWriter out;
    private BufferedReader in;
    private int state = 0;

    public ClientHandler(Socket client, AppController controller) {
        try {
            this.client = client;
            this.controller = controller;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {
                    System.out.println(line);
                    TaskManager taskManager;
                    taskManager = new SendFileTask(this);
                    if(taskManager.check(line)) taskManager.execute(line);
                    taskManager = new EraseFileTask(this);
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

    public void receiveFile(String fileName, int size) {
        try {
            FileReceiver fileReceiver = new FileReceiver(Program.PATH);
            fileReceiver.receiveFile(client.getInputStream(), fileName, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

