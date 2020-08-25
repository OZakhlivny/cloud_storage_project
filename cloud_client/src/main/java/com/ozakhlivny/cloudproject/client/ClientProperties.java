package com.ozakhlivny.cloudproject.client;

import java.io.InputStream;
import java.util.Properties;

public class ClientProperties {
    private final Properties properties;

    public ClientProperties() throws Exception{
        InputStream inputStream = getClass().getResourceAsStream("/client.properties");
        this.properties = new Properties();
        properties.load(inputStream);
    }

    public String getDefaultServerHost() {
        return properties.getProperty("DEFAULT_SERVER_HOST");
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

    public String getUserLocalDirectory() {
        return properties.getProperty("USER_LOCAL_DIRECTORY");
    }

}
