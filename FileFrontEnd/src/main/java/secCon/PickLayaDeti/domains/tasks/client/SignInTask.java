package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.AesKeyManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInTask implements TaskManager {
    private final ClientHandler handler;
    private final Hasher hasher;
    private Matcher matcher;

    public SignInTask(ClientHandler clientHandler) {
        this.handler = clientHandler;
        this.hasher = new Hasher();
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNIN) ([a-zA-Z0-9]{5,20}) ([a-zA-Z0-9]{5,50})$");
         this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // 1. Sépare le regex
        var name = matcher.group(2);
        var clearPassword = matcher.group(3);

        // 2. Vérifier login + mdp correspondant
        var users = handler.getUsers();

        try {
            // 3. Hachage du mdp
            var hashedPassword = hasher.clearTextToHash(clearPassword);
            var currentUser = users.getUsersByLoginAndPassword(name, hashedPassword);
            if (currentUser == null) return;

            handler.setCurrentUser(currentUser);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
