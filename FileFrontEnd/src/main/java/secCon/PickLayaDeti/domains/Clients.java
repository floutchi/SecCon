package secCon.PickLayaDeti.domains;

import org.json.simple.JSONObject;
import secCon.PickLayaDeti.repository.JSONConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Clients {

    String aesKey;
    String login;
    String hashPass;
    List<StoredFiles> filesList;

    public Clients(String aesKey, String login, String hashPass, List<StoredFiles> filesList) {
        this.aesKey = aesKey;
        this.login = login;
        this.hashPass = hashPass;
        this.filesList = filesList;
    }

    public void parsetoJson() {
        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("hashPass", hashPass);
        obj.put("aesKey", aesKey);
        for (StoredFiles file : filesList) {
            obj.put("stored_files", file.toJson());
        }

        try(FileWriter fileWriter = new FileWriter("C:\\dev\\reseau\\secCon\\ProjetReseau-secCon-PickLayaDeti\\FileFrontEnd\\src\\main\\java\\secCon\\PickLayaDeti\\repository\\config.json")) {
            fileWriter.write(new JSONConfig().getJSONString() + obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
