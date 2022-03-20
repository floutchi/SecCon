package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol de connexion de l'utilisateur.
 * Ce protocol est envoyé par le client python.
 */
public class SignInTask implements TaskManager {
    private final ClientHandler handler;
    private Matcher matcher;

    public SignInTask(ClientHandler clientHandler) {
        this.handler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNIN) ([a-zA-Z0-9]{5,20}) ([a-zA-Z0-9]{5,50})$");
         this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // Récupération du nom et mdp.
        var name = matcher.group(2);
        var clearPassword = matcher.group(3);

        // Récupérations des utilisateurs
        var users = handler.getUsers();

        // Obtention de notre utilisateur.
        var currentUser = users.getUsersByLoginAndPassword(name, clearPassword);
        if (currentUser == null) return;
        handler.setCurrentUser(currentUser);
    }
}
