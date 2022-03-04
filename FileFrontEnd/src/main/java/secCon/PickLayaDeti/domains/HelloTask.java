package secCon.PickLayaDeti.domains;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloTask implements TaskProcessor {

    @Override
    public boolean check(String message){
        Pattern pattern = Pattern.compile("^(HELLO) ([a-zA-Z]{5,20}.) ([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message){
        //decompose le regex et executer le code en fonction
    }
}
