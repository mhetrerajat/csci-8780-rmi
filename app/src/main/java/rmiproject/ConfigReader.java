package rmiproject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.io.InputStream;

public class ConfigReader {
    private static Properties properties;

    // Properties
    private static final String SERVER_HOST_KEY = "server.host";
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String REMOTE_OBJECT_BIND_NAME_KEY = "remoteObjectBindName";
    private static final String REMOTE_ARRAY_CAPACITY_KEY = "array.capacity";
    private static final String REMOTE_ARRAY_INIT_VALUE_KEY = "array.initValue";

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

    private static List<String> getPropertyAsList(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
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
        return ConfigReader.getProperty(REMOTE_OBJECT_BIND_NAME_KEY);
    }

    public static List<String> getRemoteArrayInitValue() {
        return ConfigReader.getPropertyAsList(REMOTE_ARRAY_INIT_VALUE_KEY);
    }

    public static Integer getRemoteArrayCapacity() {
        return ConfigReader.getIntProperty(REMOTE_ARRAY_CAPACITY_KEY);
    }
}
