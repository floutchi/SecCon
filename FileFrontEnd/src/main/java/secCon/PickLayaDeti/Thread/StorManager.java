package secCon.PickLayaDeti.Thread;

import secCon.PickLayaDeti.domains.StorProcessor;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StorManager {
    List<StorProcessor> servers;

    public StorManager() {
        this.servers = new ArrayList<>();
    }

    public void addStorage(StorProcessor server) {
        System.out.printf("[StorManager][addStorage] added [%d] %s", server.getPort(), server.getDomain());
        servers.add(server);
    }
}
