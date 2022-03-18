package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.domains.tasks.sbe.EraseResultTask;
import secCon.PickLayaDeti.domains.tasks.sbe.RetrieveResultTask;
import secCon.PickLayaDeti.domains.tasks.sbe.SendResultTask;
import secCon.PickLayaDeti.security.Hasher;

import java.util.ArrayList;
import java.util.List;

public class StorManager {
    List<StorProcessor> servers;

    private ClientHandler clientHandler;

    private StorProcessor currentStorProcessor;

    private List<Task> tasks;

    public StorManager() {
        this.tasks = new ArrayList<>();
        this.servers = new ArrayList<>();

    }

    /*public void addStorage(ServerInfo server) {
        System.out.printf("[StorManager][addStorage] added [%d] %s\r\n", server.getPort(), server.getDomain());
        servers.put(server.getDomain(), server.getPort());
    }*/

    /*public boolean isSBEAlreadyIn(ServerInfo server) {
        return servers.containsKey(server.getDomain());
    }*/

    public void createProcessor(ServerInfo infos) {

        for (StorProcessor cprocessor : servers) {
            if (!cprocessor.getServerInfo().getDomain().equals(infos.getDomain())) {
                var processor = new StorProcessor(infos, this, new Hasher());
                servers.add(processor);
                new Thread(processor).start();
            }
        }

        if(servers.isEmpty()) {
            var processor = new StorProcessor(infos, this, new Hasher());
            servers.add(processor);
            new Thread(processor).start();
        }
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void addTask(Task t) {
        this.tasks.add(t);

        for (StorProcessor sp : servers) {
            if(!sp.isBusy()) {
                sp.setTask(t);
                currentStorProcessor = sp;
            }
        }

    }

    public void resultTask(String message) {
        TaskManager taskManager;
        taskManager = new SendResultTask(clientHandler, currentStorProcessor.getServerInfo().getDomain());


        if (taskManager.check(message)) taskManager.execute(message);
        taskManager = new EraseResultTask(clientHandler);
        if (taskManager.check(message)) taskManager.execute(message);


        taskManager = new RetrieveResultTask(clientHandler, this);
        if(taskManager.check(message)) taskManager.execute(message);
    }

    public StorProcessor getStorProcessor(String fileName) {
        String domain = clientHandler.getStorProcessorOfUser(fileName);
        for (StorProcessor s : servers) {
            if(s.getServerInfo().getIpAddress().equals(domain)) {
                return s;
            }
        }

        return null;
    }

}
