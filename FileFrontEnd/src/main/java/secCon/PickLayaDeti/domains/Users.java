package secCon.PickLayaDeti.domains;

import secCon.PickLayaDeti.Program;

import java.util.List;

public class Users {

    List<User> userList;

    public List<User> getUserList() {
        return userList;
    }

    public Users() {
        this.userList = Program.jsonConfig.readUsers();
    }
}
