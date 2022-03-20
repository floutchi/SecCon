package secCon.PickLayaDeti.domains.tasks.client;

import org.mindrot.jbcrypt.BCrypt;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.AesKeyManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol de création d'un compte.
 * Ce protocol est envoyé par le client python.
 */
public class SignUpTask implements TaskManager {
    private final AesKeyManager keyManager;
    private final ClientHandler handler;
    private Matcher matcher;

    public SignUpTask(ClientHandler clientHandler) {
        this.keyManager = new AesKeyManager();
        this.handler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNUP) ([a-zA-Z0-9]{5,20}) ([a-zA-Z0-9]{5,50})$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        try {
            // Récupération du login et mdp
            var login = matcher.group(2);
            var clearTextPassword = matcher.group(3);

            // Hachage du mdp
            var hashPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

            // Génération de la clé AES
            var key = keyManager.generateAesKey();
            User newUser = new User(key, login, hashPassword, new ArrayList<>());
            addUserIfLoginIsValid(login, newUser);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajoute notre utilisateur si et SEULEMENT si, son login n'existe pas déjà dans les comptes enregistrés.
     * @param login le nom de l'utilisateur
     * @param newUser notre nouvel utilisation.
     */
    private void addUserIfLoginIsValid(String login, User newUser) {
        Users users = getUsers();

        if (users.checkUserLogin(login)) {
            users.addUser(newUser);
            handler.setCurrentUser(newUser);
        }
    }

    /**
     * Récupère tous nos utilisateurs enregistrés.
     * @return un objet "Users"
     */
    private Users getUsers() {
        return handler.getUsers();
    }
}
