package secCon.PickLayaDeti.domains;

import org.mindrot.jbcrypt.BCrypt;
import secCon.PickLayaDeti.Program;

import java.util.List;

/**
 * Déclare la classe qui va contenir la totalité des utilisateurs répertoriés dans notre fichier JSON.
 */
public class Users {

    List<User> userList;

    public List<User> getUserList() {
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
     * @param login le login à vérifier.
     * @return l'égalité (ou non) du login.
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
     * @param user utilisateur à ajouter.
     */
    public void addUser(User user) {
        userList.add(user);
        Program.jsonConfig.setUserList(getUserList());
        Program.jsonConfig.writeUsers();
    }

    /**
     * Vérifie que le compte correspond et existe dans notre liste.
     * @param login notre login à vérifier.
     * @param password le mot-de-passe à vérifier.
     * @return l'utilisateur (ou null si rien n'est trouvé).
     */
    public User getUsersByLoginAndPassword(String login, String password) {
        for (var currentUser: userList) {
            var currentLogin = currentUser.getLogin();
            var currentPassword = currentUser.getHashPass();
            if (currentLogin.equals(login) && BCrypt.checkpw(password, currentPassword)) {
                return currentUser;
            }
        }
        return null;
    }
}
