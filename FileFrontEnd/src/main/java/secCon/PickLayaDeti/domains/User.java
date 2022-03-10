package secCon.PickLayaDeti.domains;

import java.util.List;

public class User {

    String aesKey;
    String login;
    String hashPass;
    List<StoredFiles> filesList;

    public User(String aesKey, String login, String hashPass, List<StoredFiles> filesList) {
        this.aesKey = aesKey;
        this.login = login;
        this.hashPass = hashPass;
        this.filesList = filesList;
    }

    public String getAesKey() {
        return aesKey;
    }

    public String getLogin() {
        return login;
    }

    public String getHashPass() {
        return hashPass;
    }

    public List<StoredFiles> getFilesList() {
        return filesList;
    }
}
