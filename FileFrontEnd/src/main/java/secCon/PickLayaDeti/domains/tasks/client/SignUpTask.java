package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTask implements TaskManager {
    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNUP) ([a-zA-Z0-9]{5,20}) ([a-zA-Z0-9]{5,50})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // 1. Desplit le regex
        // 2. Verifier si login existe : Si oui, sortie
        // 3. Générer clé AES
        // 4. Hachage mdp (SHA-384)
        // 5. Ajout clé + login + mdp haché dans fichier Json

    }
}
