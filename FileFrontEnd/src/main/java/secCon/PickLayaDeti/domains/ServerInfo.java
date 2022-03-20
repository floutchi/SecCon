package secCon.PickLayaDeti.domains;

/**
 * Crée un objet "ServerInfo" afin de pouvoir le passer à travers différents objets et éviter d'avoir des chaines volantes (limite
 * également les paramètres passés aux différentes méthodes).
 */
public class ServerInfo {

    private final String domain;
    private final int port;

    /**
     * Constructeur de notre objet "ServerInfo"
     * @param domain Le domaine du serveur sur lequel nous sommes connectés.
     * @param port Le port du serveur sur lequel nous sommes connectés.
     */
    public ServerInfo(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    /**
     * Retourne le domaine du serveur précédemment créé.
     * @return le domaine
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Retourne le port de l'objet serveur précédemment créé.
     * @return le port
     */
    public int getPort() {
        return port;
    }
}
