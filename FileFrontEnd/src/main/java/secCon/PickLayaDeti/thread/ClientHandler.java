package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.client.*;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private Users users;

    public ClientHandler(Socket client) {

        this.users = new Users();

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
                    execute(line);

                } else {
                    stop = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try { client.close(); } catch (IOException ex) {}
        }

        Program.jsonConfig.setUserList(users.getUserList());
        Program.jsonConfig.writeUsers();

    }

    private void listeningForClient() {

    }

    private void execute(String line) {
        TaskManager taskManager;
        taskManager = new SignUpTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new SignOutTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new SignInTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new SaveFileTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new RemoveFileTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new GetFileTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new GetFileTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
        taskManager = new FileListTask();
        if(taskManager.check(line)) {
            taskManager.execute(line);
        }
    }

}
