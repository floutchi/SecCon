package secCon.PickLayaDeti.repository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class JSONConfig {

    private JSONObject configObject;
    private final String uniqueID;
    private final String path;
    private final String multicastAddress;
    private final int multicastPort;
    private final int multicastDelayInSeconds;
    private final int unicastPort;
    private final String jsonPath = Paths.get("StorBackEnd", "/src/main/java/secCon/PickLayaDeti/repository/config.json").toAbsolutePath().toString();

    /**
     * Constructeur permettant d'initialiser les attributs du SBE
     * Il va lire le fichier JSON et instancier l'identifiant du SBE,
     * le chemin d'enregistrement des fichiers, l'adresse multicast et son port,
     * le délai d'envoi du multicast et le port unicast.
     */
    public JSONConfig() {
        readJson();
        this.uniqueID = (String) configObject.get("uniqueID");
        this.path = (String) configObject.get("path");
        this.multicastAddress = (String) configObject.get("multicastAddress");;
        this.multicastPort = ((Long) configObject.get("multicastPort")).intValue();
        this.multicastDelayInSeconds = ((Long) configObject.get("multicastDelayInSeconds")).intValue();
        this.unicastPort = ((Long) configObject.get("unicastPort")).intValue();
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

    /**
     * @return l'identifiant du SBE
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * @return Le chemin d'enregistrement des fichiers du SBE
     */
    public String getPath() {
        return path;
    }

    /**
     * @return Adresse multicast d'envoi du HELLO
     */
    public String getMulticastAddress() {
        return multicastAddress;
    }

    /**
     * @return Port multicast d'envoi du HELLO
     */
    public int getMulticastPort() {
        return multicastPort;
    }

    /**
     * @return Délai d'envoi du message multicast
     */
    public int getMulticastDelayInSeconds() {
        return multicastDelayInSeconds;
    }

    /**
     * @return Port unicast sur lequel le FFE peut contacter le SBE
     */
    public int getUnicastPort() {
        return unicastPort;
    }
}
