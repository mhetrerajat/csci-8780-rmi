package rmiproject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigReader {

    private static final Logger logger = Logger.getLogger(ConfigReader.class.getName());

    private Properties properties;

    // Properties
    private final String SERVER_HOST_KEY = "server.host";
    private final String SERVER_PORT_KEY = "server.port";
    private final String REMOTE_OBJECT_BIND_NAME_KEY = "remoteObjectBindName";
    private final String REMOTE_ARRAY_CAPACITY_KEY = "array.capacity";
    private final String REMOTE_ARRAY_INIT_VALUE_KEY = "array.initValue";
    private final String LOCK_RELEASE_TIMEOUT_KEY = "lock.timeout";

    public ConfigReader(String configPath) {

        logger.info(String.format("Loading config file path: %s", configPath));

        // Parse properties
        properties = new Properties();

        try (InputStream configFile = ConfigReader.class.getResourceAsStream(configPath)) {
            if (configFile != null) {
                properties.load(configFile);
            } else {
                Path path = Paths.get(configPath);
                InputStream inputStream = Files.newInputStream(path);
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error loading configuration from %s", configPath), e);
        }
    }

    public static String parseConfigPathFromCLI(String[] args, String defaultConfigFile) {
        String configFileOption = System.getProperty("config");
        return !configFileOption.isBlank() ? configFileOption : defaultConfigFile;
    }

    private String getProperty(String key) {
        return properties.getProperty(key);
    }

    private int getIntProperty(String key) {
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

    private List<String> getPropertyAsList(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Property not found for key: " + key);
    }

    public String getServerHost() {
        return getProperty(SERVER_HOST_KEY);
    }

    public Integer getServerPort() {
        return getIntProperty(SERVER_PORT_KEY);
    }

    public String getRemoteObjectBindName() {
        return getProperty(REMOTE_OBJECT_BIND_NAME_KEY);
    }

    public List<String> getRemoteArrayInitValue() {
        return getPropertyAsList(REMOTE_ARRAY_INIT_VALUE_KEY);
    }

    public Integer getRemoteArrayCapacity() {
        return getIntProperty(REMOTE_ARRAY_CAPACITY_KEY);
    }

    public long getLockAutoReleaseTimeout() {
        return getIntProperty(LOCK_RELEASE_TIMEOUT_KEY);
    }
}
