package secCon.PickLayaDeti.domains;

public class StoredFiles {

    String name;
    String iv;
    String storageProvider;
    int size;

    public StoredFiles(String name, String iv, int size, String storageProvider) {
        this.name = name;
        this.iv = iv;
        this.size = size;
        this.storageProvider = storageProvider;
    }

    public String getName() {
        return name;
    }

    public String getIv() {
        return iv;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public int getSize() {
        return size;
    }
}
