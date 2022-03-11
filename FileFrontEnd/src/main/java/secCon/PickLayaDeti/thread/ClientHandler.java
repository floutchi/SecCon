package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.client.*;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Users users;
    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private User connectedUser;

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
        var tasks = createTask();
        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {
                    System.out.println("[ClientHandler][run] received: " + line);
                    executeTask(line, tasks);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try { client.close(); } catch (IOException ex) {}
        }

        Program.jsonConfig.setUserList(users.getUserList());
        Program.jsonConfig.writeUsers();

    }

    private void executeTask(String message, List<TaskManager> tasks) {
        for (var currentTask:
             tasks) {
            if (currentTask.check(message)) currentTask.execute(message);
        }
    }

    public Users getUsers() {
        return users;
    }

    public void setCurrentUser(User user) {
        this.connectedUser = user;
    }

    public void disconnectAndStopConnexion() {
        stop = true;
        connected = false;
        connectedUser = null;
    }

    private List<TaskManager> createTask() {
        var tasks = new ArrayList<TaskManager>();
        // region Ajouts Tâches
        tasks.add(new SignUpTask(this));
        tasks.add(new SignOutTask(this));
        tasks.add(new SignInTask(this));
        tasks.add(new SaveFileTask(this));
        tasks.add(new RemoveFileTask(this));
        tasks.add(new GetFileTask(this));
        tasks.add(new FileListTask(this));
        // endregion
        return tasks;
    }

}
