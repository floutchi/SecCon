package secCon.PickLayaDeti.domains;

import org.json.simple.JSONObject;


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

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        jo.put("filename", name);
        jo.put("filesize", size);
        jo.put("storage_provider", storageProvider);
        jo.put("hashed_name", hashedName);
        return jo;
    }
}
