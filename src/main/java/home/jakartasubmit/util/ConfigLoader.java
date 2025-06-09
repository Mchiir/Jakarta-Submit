package home.jakartasubmit.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    public static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
        return props;
    }
}