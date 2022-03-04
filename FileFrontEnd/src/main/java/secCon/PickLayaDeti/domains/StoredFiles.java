package secCon.PickLayaDeti.domains;

import java.util.List;

public class StoredFiles {

    String name;
    String hashedName;
    List<String> storageProvider;
    int size;

    public StoredFiles(String name, String hashedName, int size) {
        this.name = name;
        this.hashedName = hashedName;
        this.size = size;
    }
}
