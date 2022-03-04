package secCon.PickLayaDeti.repository;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONConfig {

    private JSONObject configObject;
    private final String multicastAddress;
    private final int multicastPort;
    private final String path;

    public JSONConfig() {
        readJson();
        this.multicastAddress = (String) configObject.get("multicastAddress");
        this.multicastPort = ((Long) configObject.get("multicastPort")).intValue();
        this.path = (String) configObject.get("path");
    }

    public String getJSONString() {
        return configObject.toJSONString();
    }

    private void readJson() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("C:\\dev\\reseau\\secCon\\ProjetReseau-secCon-PickLayaDeti\\FileFrontEnd\\src\\main\\java\\secCon\\PickLayaDeti\\repository\\config.json")) {
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

    public String getPath() {
        return path;
    }
}
