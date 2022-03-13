package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.tasks.interfaces.TaskManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendFileTask implements TaskManager {
    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SENDFILE) ([a-zA-Z0-9]{50,200})(.jpg|.JPG|.gif|.GIF|.doc|.DOC|.pdf|.PDF|.png) ([0-9]{1,10}) ([a-zA-Z0-9]{50,200})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
