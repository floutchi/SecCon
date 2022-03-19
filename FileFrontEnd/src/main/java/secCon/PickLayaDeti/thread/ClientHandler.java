package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.client.*;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiverEncrypt;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.fileManager.FileSenderDecrypt;
import secCon.PickLayaDeti.security.Hasher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Users users;
    private final StorManager storManager;
    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private User connectedUser;

    private String currentFileName;
    private int currentFileSize;

    private String currentIv;


    public ClientHandler(Socket client, StorManager storManager) {
        this.storManager = storManager;
        this.users = new Users();
        try {
            this.client = client;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        var tasks = createTask();
        try {
            while (connected && !stop) {
                String line = in.readLine();
                if (line != null) {
                    if (line.length() < 100) {
                        //System.out.println("[ClientHandler][run] received: " + line);
                    }
                    executeTask(line, tasks);
                }
            }
        } catch (IOException e) {
            System.out.println("[ClientHandler][run] Client déconnecté");
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Program.jsonConfig.setUserList(users.getUserList());
        Program.jsonConfig.writeUsers();

    }

    private void executeTask(String message, List<TaskManager> tasks) {
        if (connectedUser == null) if (setUpConnectedUser(message)) return;
        for (var currentTask : tasks) {
            if (currentTask.check(message)) currentTask.execute(message);
        }
    }

    private boolean setUpConnectedUser(String message) {
        var initialTasks = connexionTasks();
        for (var currentTask : initialTasks) {
            if (currentTask.check(message)) {
                currentTask.execute(message);
            }
        }

        if (connectedUser == null) {
            sendMessage("SIGN_ERROR");
            return true;
        } else {
            sendMessage("SIGN_OK");
        }
        return false;
    }

    public void sendMessage(String message) {
        if (connected) {
            System.out.println("[ClientHandler][sendMessage] " + message);
            out.print(String.format("%s\r\n", message));
            out.flush();
        }
    }

    public Users getUsers() {
        return users;
    }

    public void setCurrentUser(User user) {
        this.connectedUser = user;
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public void disconnectAndStopConnexion() {
        stop = true;
        connected = false;
        connectedUser = null;
    }

    private List<TaskManager> connexionTasks() {
        var tasks = new ArrayList<TaskManager>();
        tasks.add(new SignUpTask(this));
        tasks.add(new SignOutTask(this));
        tasks.add(new SignInTask(this));
        return tasks;
    }

    private List<TaskManager> createTask() {
        var tasks = new ArrayList<TaskManager>();
        // region Ajouts Tâches
        tasks.add(new SaveFileTask(this, storManager));
        tasks.add(new RemoveFileTask(this, storManager));
        tasks.add(new GetFileTask(this, storManager));
        tasks.add(new FileListTask(this));
        // endregion
        return tasks;
    }

    public boolean receiveFile(String name, int size) throws IOException {

        this.currentFileName = name;
        this.currentFileSize = size;

        // Generate key
        byte[] decodedKey = Base64.getDecoder().decode(this.connectedUser.getAesKey());
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        this.currentIv = Base64.getEncoder().encodeToString(IV);
        FileReceiverEncrypt receiver = new FileReceiverEncrypt(Program.PATH, aesKey, IV);
        return receiver.receiveFile(client.getInputStream(), name, size);
    }

    public void sendFile(String name) throws IOException {
        this.currentFileName = name;
        Hasher h = new Hasher();
        String hashedName = "";
        hashedName = h.clearTextToHash(name);

        byte[] decodedKey = Base64.getDecoder().decode(this.connectedUser.getAesKey());
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] IV = new byte[12];
        var fileList = connectedUser.getFilesList();
        for (StoredFiles f : fileList) {
            if(f.getName().equals(name)) {
                IV = Base64.getDecoder().decode(f.getIv());
            }
        }

        FileSenderDecrypt sender = new FileSenderDecrypt(Program.PATH, aesKey, IV);
        sender.sendFile(hashedName, client.getOutputStream());
    }

    public String getCurrentIv() {
        return currentIv;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public String getStorProcessorOfUser(String fileName) {
        return connectedUser.getStorageManagerOfFile(fileName, new Hasher());
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileName(String name) {
        this.currentFileName = name;
    }
}
