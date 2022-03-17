package secCon.PickLayaDeti.tasks.interfaces;

import java.io.IOException;

public interface TaskManager {

    boolean check(String message);

    void execute(String message) throws IOException;
}
