package secCon.PickLayaDeti.tasks.interfaces;

import java.io.IOException;

public interface TaskManager {

    /**
     * Vérifie avec une regex que le message reçu est bien valide
     * @param message   Message reçu
     * @return          Vrai si le message reçu est valide, Faux sinon
     */
    boolean check(String message);

    /**
     * Exécute la tâche
     * @param message   Message reçu et valide
     * @throws IOException
     */
    void execute(String message) throws IOException;
}
