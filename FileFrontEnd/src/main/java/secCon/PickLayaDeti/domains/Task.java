package secCon.PickLayaDeti.domains;

import secCon.PickLayaDeti.thread.ClientHandler;

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

    private String type;
    private String name;
    private String command;
    private ClientHandler client;
    private String idBackEnd;
    private String status;

    public Task(String type, String name, String command, ClientHandler client, String idBackEnd, String status) {
        this.type = type;
        this.name = name;
        this.command = command;
        this.client = client;
        this.idBackEnd = idBackEnd;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public ClientHandler getClient() {
        return client;
    }

    public String getIdBackEnd() {
        return idBackEnd;
    }

    public String getStatus() {
        return status;
    }
}
