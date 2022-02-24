package secCon.PickLayaDeti;

import secCon.PickLayaDeti.Thread.ClientRunnable;

public interface AppController {
    void broadcastToAllClientsExceptMe(ClientRunnable clientRunnable, String line);
    void registerClient(ClientRunnable clientRunnable);
    void unregisterClient(ClientRunnable clientRunnable);
}
