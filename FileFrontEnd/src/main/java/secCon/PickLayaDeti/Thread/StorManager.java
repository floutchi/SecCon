package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;

import java.util.HashMap;
import java.util.Map;

public class StorManager {
    Map<String, Integer> servers;

    public StorManager() {
        this.servers = new HashMap<>();
    }

    public void addStorage(ServerInfo server) {
        System.out.printf("[StorManager][addStorage] added [%d] %s\r\n", server.getPort(), server.getDomain());
        servers.put(server.getDomain(), server.getPort());
    }

    public boolean isSBEAlreadyIn(ServerInfo server) {
        return servers.containsKey(server.getDomain());
    }
}
