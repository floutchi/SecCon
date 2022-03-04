package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;

public class RetrieveResultTask implements TaskManager {
    @Override
    public boolean check(String message) {
        return false;
    }

    @Override
    public void execute(String message) {

    }
}
