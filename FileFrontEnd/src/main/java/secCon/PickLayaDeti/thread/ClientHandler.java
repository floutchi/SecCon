package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.client.*;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket client) {
        try {
            this.client = client;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        listeningForClient();

        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {

                    System.out.println("[ClientHandler][run] received: " + line);
                    readLineAndExecute(line);

                } else {
                    stop = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try { client.close(); } catch (IOException ex) {}
        }

    }

    private void listeningForClient() {

    }

    private void readLineAndExecute(String message) {
        for (var currentTask : getAllTasks()) {
            if (currentTask.check(message)) {
                currentTask.execute(message);
            }
        }
    }

    private List<TaskManager> getAllTasks() {
        List<TaskManager> allTasks = new ArrayList<>();
        // region Ajout de tâches
        allTasks.add(new SignUpTask());
        allTasks.add(new SignOutTask());
        allTasks.add(new SignInTask());
        allTasks.add(new SaveFileTask());
        allTasks.add(new RemoveFileTask());
        allTasks.add(new GetFileTask());
        allTasks.add(new FileListTask());
        // endregion
        return allTasks;
    }
}
