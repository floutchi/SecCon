package secCon.PickLayaDeti.domains;

/**
 * Déclare notre classe de tâche pour les délégués aux différents storProcessors.
 */
public class Task {

    private final String destination;
    private final String protocol;

    private String fileName = "";
    private int fileSize = 0;

    /**
     * Déclare le constructeur comprenant :
     * @param protocol le protocol demandé par le ClientHandler.
     * @param destination le domaine de destination (ou null si pas précisé)
     */
    public Task(String protocol, String destination) {
        this.protocol = protocol;
        this.destination = destination;
    }

    /**
     * Défini le nom du fichier.
     * @param fileName le nom.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Défini la taille du fichier.
     * @param fileSize la taille.
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Récupère le nom de notre fichier.
     * @return le nom du fichier.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Récupère la taille du fichier.
     * @return la taille.
     */
    public int getFileSize() {
        return fileSize;
    }

    public String getProtocol() {
        return this.protocol;
    }
    public String getDestination() {
        return this.destination;
    }

}
