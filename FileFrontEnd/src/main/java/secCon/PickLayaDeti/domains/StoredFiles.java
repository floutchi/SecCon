package secCon.PickLayaDeti.domains;

public class StoredFiles {

    String name;
    String hashedName;
    String storageProvider;
    int size;

    public StoredFiles(String name, String hashedName, int size) {
        this.name = name;
        this.hashedName = hashedName;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getHashedName() {
        return hashedName;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public int getSize() {
        return size;
    }
}
