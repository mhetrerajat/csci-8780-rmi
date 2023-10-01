package rmiproject;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class ConfigReader {
    private static Properties properties;

    // Properties
    private static final String SERVER_HOST_KEY = "serverHost";
    private static final String SERVER_PORT_KEY = "serverPort";
    private static final String REMOTE_OBJECT_BIND_NAME = "remoteObjectBindName";

    static {
        properties = new Properties();
        try (InputStream configFile = ConfigReader.class.getResourceAsStream("/config.properties")) {
            if (configFile != null) {
                properties.load(configFile);
            } else {
                throw new RuntimeException("config.properties not found in resources directory");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration from config.properties", e);
        }
    }

    private static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static int getIntProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Error parsing integer value for key: " + key, e);
            }
        }
        throw new RuntimeException("Property not found for key: " + key);
    }

    public static String getServerHost() {
        return ConfigReader.getProperty(SERVER_HOST_KEY);
    }

    public static Integer getServerPort() {
        return ConfigReader.getIntProperty(SERVER_PORT_KEY);
    }

    public static String getRemoteObjectBindName() {
        return ConfigReader.getProperty(REMOTE_OBJECT_BIND_NAME);
    }
}
