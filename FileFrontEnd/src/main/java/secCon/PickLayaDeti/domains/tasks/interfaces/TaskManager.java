package secCon.PickLayaDeti.domains.tasks.interfaces;

public interface TaskManager {

    /**
     * Vérifie la validité de la regex afin de déléguer la responsabilité à la bonne tâche.
     * @param message le protocol envoyé à vérifier
     * @return vrai / faux en fonction de la validité de la regex.
     */
    boolean check(String message);

    /**
     * Exécute le protocole associé à notre tâche.
     * @param message le protocole envoyé
     */
    void execute(String message);

}
