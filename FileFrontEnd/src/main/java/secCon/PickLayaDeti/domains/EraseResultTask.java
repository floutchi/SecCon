package secCon.PickLayaDeti.domains;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseResultTask implements TaskProcessor {
    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASE_OK | ERASE_ERROR)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
