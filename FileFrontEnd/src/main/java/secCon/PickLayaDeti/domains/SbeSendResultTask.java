package secCon.PickLayaDeti.domains;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SbeSendResultTask implements TaskProcessor{
    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SEND_ERROR|SEND_OK)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
