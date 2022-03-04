package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveFileTask implements TaskManager {

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(REMOVEFILE) ([a-zA-Z0-9]{5,20})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
