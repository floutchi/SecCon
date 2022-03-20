package secCon.PickLayaDeti.domains;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.security.Hasher;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Classe décidant d'un utilisateur avec la totalité de ses attributs.
 */
public class User {

    private final String aesKey;
    private final String login;
    private final String hashPass;
    private final List<StoredFiles> filesList;

    /**
     * Constructeur de notre classe.
     * @param aesKey Utile pour le chiffrement
     * @param login le login de l'utilisateur
     * @param hashPass le mot de passé HASHE
     * @param filesList la liste des fichiers de l'utilisateur
     */
    public User(String aesKey, String login, String hashPass, List<StoredFiles> filesList) {
        this.aesKey = aesKey;
        this.login = login;
        this.hashPass = hashPass;
        this.filesList = filesList;
    }

    /**
     * Récupère la clé AES de notre utilisateur.
     * @return la clé AES
     */
    public String getAesKey() {
        return aesKey;
    }

    /**
     * Récupère le login de notre utilisateur.
     * @return le login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Récupère le mot de passé hashé de notre utilisateur.
     * @return le mdp hashé.
     */
    public String getHashPass() {
        return hashPass;
    }

    /**
     * Récupère la totalité des fichiers de notre utilisateurs.
     * @return la liste des fichiers.
     */
    public List<StoredFiles> getFilesList() {
        return filesList;
    }

    /**
     * Ajoute un fichier et met ceci à jour dans notre fichier JSON
     * @param storedFile le fichier à ajouter.
     */
    public void addFile(StoredFiles storedFile) {
        filesList.add(storedFile);
        Program.jsonConfig.updateUser(this);
        Program.jsonConfig.writeUsers();
    }

    /**
     * Supprime un fichier sur base de son nom.
     * @param name le nom du fichier.
     */
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

    /**
     * Récupère le nom de domaine sur lequel notre fichier est actuellement stocké.
     * @param fileName le nom du fichier.
     * @param hasher l'objet hasher afin de vérifier les empreintes.
     * @return le nom de domaine.
     */
    public String getStorageManagerOfFile(String fileName, Hasher hasher) {
        try {
            for (StoredFiles f : filesList) {
                var name = hasher.clearTextToHash(f.getName());
                if (isNameEquals(fileName, name)) return f.getStorageProvider();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Vérifie l'égalité entre nos deux noms.
     * @param fileName le nom du fichier souhaité.
     * @param name le nom courrant.
     * @return l'égalité (ou non) des deux noms.
     */
    private boolean isNameEquals(String fileName, String name) {
        return name.equals(fileName);
    }

}
