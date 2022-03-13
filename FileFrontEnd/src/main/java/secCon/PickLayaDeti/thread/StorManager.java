package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.domains.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorManager {
    List<StorProcessor> servers;
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
                var processor = new StorProcessor(infos, this);
                servers.add(processor);
                new Thread(processor).start();
            }
        }

        if(servers.isEmpty()) {
            var processor = new StorProcessor(infos, this);
            servers.add(processor);
            new Thread(processor).start();
        }
    }

    public void addTask(Task t) {
        this.tasks.add(t);
    }

    public Task askTask() {
        for (Task t : tasks) {
            if (servers.get(0) != null) {
                return t;
            }
        }
        return null;
    }
}
