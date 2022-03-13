package secCon.PickLayaDeti.domains;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.security.Hasher;

import java.util.List;

public class Users {

    List<User> userList;
    private Hasher hasher;

    public List<User> getUserList() {
        this.hasher = new Hasher();
        return userList;
    }

    /**
     * Instancie l'utilisateur en lisant le fichier JSON.
     */
    public Users() {
        this.userList = Program.jsonConfig.readUsers();
    }

    /**
     * Vérifie le login de notre utilisateur.
     * @param login
     * @return
     */
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

    /**
     * Ajoute un utilisateur à notre liste.
     * @param user
     */
    public void addUser(User user) {
        userList.add(user);
        Program.jsonConfig.setUserList(getUserList());
        Program.jsonConfig.writeUsers();
    }

    /**
     * Vérifie que le login et le mdp existe bel et bien dans notre liste.
     * @param login
     * @param password
     * @return
     */
    public User getUsersByLoginAndPassword(String login, String password) {
        for (var currentUser: userList) {
            var currentLogin = currentUser.getLogin();
            var currentPassword = currentUser.getHashPass();
            if (currentLogin.equals(login) && currentPassword.equals(password)) {
                return currentUser;
            }
        }
        return null;
    }
}
