package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.domains.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorManager {
    Map<String, Integer> servers;
    private List<Task> tasks;

    public StorManager() {
        this.tasks = new ArrayList<>();
        this.servers = new HashMap<>();
    }

    public void addStorage(ServerInfo server) {
        System.out.printf("[StorManager][addStorage] added [%d] %s\r\n", server.getPort(), server.getDomain());
        servers.put(server.getDomain(), server.getPort());
    }

    public boolean isSBEAlreadyIn(ServerInfo server) {
        return servers.containsKey(server.getDomain());
    }

    public void createProcessor(ServerInfo infos) {
        var processor = new StorProcessor(infos, this);
        new Thread(processor).start();
    }

    public void addTask(Task t) {
        this.tasks.add(t);
    }

    public void askTask() {
    }
}
