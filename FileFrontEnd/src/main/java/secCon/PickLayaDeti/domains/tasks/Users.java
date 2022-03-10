package secCon.PickLayaDeti.domains.tasks;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.repository.JSONConfig;

import java.util.List;

public class Users {

    List<User> userList;

    public void Users() {
        this.userList = Program.jsonConfig.readUsers();
    }
}
