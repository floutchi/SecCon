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
