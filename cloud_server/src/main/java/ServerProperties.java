import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {
    private final Properties properties;

    public ServerProperties() throws Exception{
        InputStream inputStream = getClass().getResourceAsStream("/server.properties");
        this.properties = new Properties();
        properties.load(inputStream);
    }

    public int getDefaultServerPort() {
        return Integer.parseInt(properties.getProperty("DEFAULT_SERVER_PORT"));
    }

    public int getMaxObjectSize() {
        return Integer.parseInt(properties.getProperty("MAX_OBJECT_SIZE"));
    }

    public int getMaxBufferSize() {
        return Integer.parseInt(properties.getProperty("MAX_BUFFER_SIZE"));
    }

    public String getServerRootPath() {
        return properties.getProperty("SERVER_ROOT_PATH");
    }

}
