package secCon.PickLayaDeti.domains.tasks;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignOutTask implements TaskManager {
    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNOUT)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
