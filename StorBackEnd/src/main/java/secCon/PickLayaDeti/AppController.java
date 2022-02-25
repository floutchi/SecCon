package secCon.PickLayaDeti;

import secCon.PickLayaDeti.thread.ClientRunnable;

public interface AppController {
    void broadcastToAllClientsExceptMe(ClientRunnable clientRunnable, String line);
    void registerClient(ClientRunnable clientRunnable);
    void unregisterClient(ClientRunnable clientRunnable);
}
