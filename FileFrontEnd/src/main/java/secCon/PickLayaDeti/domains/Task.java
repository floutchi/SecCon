package secCon.PickLayaDeti.domains;

public class Task {
   /*
    ** Le type de tâche (envoi, réception, suppression d’un fichier),
    ** Le nom du fichier (le nom original, non haché),
    ** La commande (c’est-à-dire le message du protocole qui doit être envoyé par le Processor comme ffe_sendfile, ffe_erasefile ou
       ffe_retrievefile)
    ** L’utilisateur (qui est à l’origine de cette tâche),
    ** L’identifiant du StorBackEnd concerné (qui permet au « bon » Processor d’exécuter la tâche) et enfin
    ** Le statut de cette tâche (en cours, terminée, erreur, …).*/

    //faire une premiere methode pour diviser le message ( regex )
    // faire une methode pour executer chaque operation

    private final String destination;
    private final String protocol;

    private String fileName = "";
    private int fileSize = 0;

    public Task(String protocol, String destination) {
        this.protocol = protocol;
        this.destination = destination;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

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
