package secCon.PickLayaDeti.domains;

/**
 * Déclare une classe model pour le stockage du fichier dans notre config.json
 */
public class StoredFiles {

    String name;
    String iv;
    String storageProvider;
    int size;

    /**
     * Déclare le constructeur de notre class de stockage JSON avec les différents attributs appropriés
     * @param name le nom en clair.
     * @param iv l'IV du fichier
     * @param size la taille
     * @param storageProvider le domaine de storage
     */
    public StoredFiles(String name, String iv, int size, String storageProvider) {
        this.name = name;
        this.iv = iv;
        this.size = size;
        this.storageProvider = storageProvider;
    }

    /**
     * Récupère le nom de notre fichier.
     * @return le nom
     */
    public String getName() {
        return name;
    }

    /**
     * Récupère l'IV de notre fichier.
     * @return l'IV
     */
    public String getIv() {
        return iv;
    }

    /**
     * Récupère la où est stocké notre fichier.
     * @return le domaine de stockage.
     */
    public String getStorageProvider() {
        return storageProvider;
    }

    /**
     * Récupère la taille de notre fichier.
     * @return la taille.
     */
    public int getSize() {
        return size;
    }
}
