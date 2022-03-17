package secCon.PickLayaDeti.repository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JSONConfig {

    private JSONObject configObject;
    private final String multicastAddress;
    private final int multicastPort;
    private final int unicastPort;
    private final String path;
    private List<User> users;
    private final String jsonPath = Paths.get("FileFrontEnd", "/src/main/java/secCon/PickLayaDeti/repository/config.json").toAbsolutePath().toString();

    public JSONConfig() {
        readJson();
        this.multicastAddress = (String) configObject.get("multicastAddress");
        this.multicastPort = ((Long) configObject.get("multicastPort")).intValue();
        this.unicastPort = ((Long) configObject.get("unicastPort")).intValue();
        this.path = (String) configObject.get("path");
        this.users = readUsers();
    }

    public void updateUsers(Users users) {
        this.users = users.getUserList();
    }

    public void writeUsers() {
        JSONArray userArray = new JSONArray();
        for (User u : users) {
            JSONObject object = new JSONObject();
            object.put("login", u.getLogin());
            object.put("hashpass", u.getHashPass());
            object.put("aeskey", u.getAesKey());

            JSONArray fileArrays = new JSONArray();
            for (StoredFiles f : u.getFilesList()) {
                JSONObject fileObject = new JSONObject();
                fileObject.put("filename", f.getName());
                fileObject.put("filesize", f.getSize());
                fileObject.put("iv", f.getHashedName());
                fileObject.put("storage_provider", f.getStorageProvider());

                fileArrays.add(fileObject);
            }
            object.put("stored_files", fileArrays);
            userArray.add(object);
        }

        configObject.put("users", userArray);

        try(FileWriter fw = new FileWriter(jsonPath, StandardCharsets.UTF_8)) {
            fw.write(configObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> readUsers() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonPath)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray array = (JSONArray) jsonObject.get("users");
            List<JSONObject> readUsers = new ArrayList<>();
            Map<JSONObject, List<JSONObject>> jsonMap = new HashMap();
            for (int i = 0; i < array.size(); i++) {
                JSONObject u = (JSONObject) array.get(i);
                readUsers.add(u);
                JSONArray fileArray = (JSONArray) u.get("stored_files");

                List<JSONObject> files = new ArrayList<>();
                for (int j = 0; j < fileArray.size(); j++) {
                    files.add((JSONObject) fileArray.get(j));
                }

                jsonMap.put(u, files);
            }

            List<User> clients = new ArrayList<>();

            for(JSONObject us : jsonMap.keySet()) {
                List<StoredFiles> storedFiles = new ArrayList<>();
                for (JSONObject f : jsonMap.get(us)) {
                    StoredFiles st = new StoredFiles((String)f.get("filename"), (String)f.get("iv"), ((Long)f.get("filesize")).intValue(), (String)f.get("storage_provider"));
                    storedFiles.add(st);
                }
                User c = new User((String)us.get("aeskey"), (String) us.get("login"), (String) us.get("hashpass"), storedFiles);
                clients.add(c);
            }

            return clients;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getJSONString() {
        return configObject.toJSONString();
    }

    private void readJson() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(jsonPath)) {
            Object obj = jsonParser.parse(reader);
            this.configObject = (JSONObject) obj;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }
    public int getUnicastPort() { return unicastPort; }

    public String getPath() {
        return path;
    }

    public void setUserList(List<User> userList) {
        this.users = userList;
    }
}
