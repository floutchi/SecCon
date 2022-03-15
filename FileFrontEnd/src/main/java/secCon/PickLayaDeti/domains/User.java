package secCon.PickLayaDeti.domains;

import secCon.PickLayaDeti.Program;

import java.util.List;

public class User {

    private String aesKey;
    private String login;
    private String hashPass;
    private String salt;
    private List<StoredFiles> filesList;

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

    public void addFile(StoredFiles storedFile) {
        filesList.add(storedFile);
        Program.jsonConfig.writeUsers();
    }

    public void removeFile(String name) {
        StoredFiles todelete = null;
        for (StoredFiles f : filesList) {
            if(f.getName().equals(name)) {
                todelete = f;
            }
        }
        
        filesList.remove(todelete);

        Program.jsonConfig.writeUsers();
    }

    public String getStorageManagerOfFile(String fileName) {
        for (StoredFiles f : filesList) {
            if(f.getName().equals(fileName)) {
                return f.getStorageProvider();
            }
        }
        return null;
    }

}
