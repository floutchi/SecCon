package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInTask implements TaskManager {

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNIN) ([a-zA-Z0-9]{5,20}) ([a-zA-Z0-9]{5,50})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // 1. Desplit le regex
        // 2. Vérifier login + mdp correspondant (clé AES)
    }
}
