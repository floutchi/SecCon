package secCon.PickLayaDeti.domains;

public class StorProcessor {

    private final String domain;
    private final String ipAddress;
    private final int port;

    public StorProcessor(String domain, String ipAddress, int port) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
