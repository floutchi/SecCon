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

    public boolean checkUserLogin(String login) {
        for (var currentUser:
             userList) {
            var currentUserLogin = currentUser.getLogin();
            if (currentUserLogin.equals(login)) {
                return false;
            }
        }
        return true;
    }

    public void addUser(User user) {
        userList.add(user);
    }
}
