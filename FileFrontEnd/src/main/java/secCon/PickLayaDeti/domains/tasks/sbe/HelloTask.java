package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.thread.StorProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloTask implements TaskManager {

    private final StorManager manager;
    private Matcher matcher;

    public HelloTask(StorManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean check(String message){
        Pattern pattern = Pattern.compile("^(HELLO) ([a-zA-Z].{5,20}) ([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message){
        System.out.printf("[Program] Receiving HELLO from %s with ID %s (unicast port: %s) \r\n", matcher.group(3), matcher.group(1), matcher.group(2));
        var infos = new ServerInfo(matcher.group(2), Integer.parseInt(matcher.group(3)));
        manager.createProcessor(infos);

    }
}
