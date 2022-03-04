package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.AesKeyManager;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTask implements TaskManager {
    private final AesKeyManager keyManager;
    private Matcher matcher;

    public SignUpTask() {
        this.keyManager = new AesKeyManager();
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
            var key = keyManager.generateAesKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
