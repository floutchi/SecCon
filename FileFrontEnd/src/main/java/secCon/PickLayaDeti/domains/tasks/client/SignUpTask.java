package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.AesKeyManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTask implements TaskManager {
    private final AesKeyManager keyManager;
    private final Hasher hasher;
    private final ClientHandler handler;
    private Matcher matcher;

    public SignUpTask(ClientHandler clientHandler) {
        this.keyManager = new AesKeyManager();
        this.hasher = new Hasher();
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
        // Génération de la clé AES.
        try {
            var login = matcher.group(1);
            var clearTextPassword = matcher.group(2);
            // Récupère le sel qui sera lié à l'utilisateur
            var salt = hasher.getNextSalt();
            // Hachage du mdp
            var hashPassword = hasher.hash(clearTextPassword.toCharArray(), salt);
            var key = keyManager.generateAesKey();

            // Récupère les utilisateurs
            Users users = getUsers();
            if (users.checkUserLogin(login)) {
                users.addUser(new User(key, login, Arrays.toString(hashPassword), Arrays.toString(salt), new ArrayList<>()));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Users getUsers() {
        return handler.getUsers();
    }
}
