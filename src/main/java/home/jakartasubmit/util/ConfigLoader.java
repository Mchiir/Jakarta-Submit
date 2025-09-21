package home.jakartasubmit.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigLoader {
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

    public static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            props.load(input);

            // Expand env vars
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                if (value != null) {
                    Matcher matcher = ENV_PATTERN.matcher(value);
                    StringBuffer sb = new StringBuffer();
                    while (matcher.find()) {
                        String envVar = matcher.group(1);
                        String envValue = System.getenv(envVar);
                        if (envValue == null) {
                            throw new IllegalStateException("Missing env variable: " + envVar);
                        }
                        matcher.appendReplacement(sb, envValue);
                    }
                    matcher.appendTail(sb);
                    props.setProperty(key, sb.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
        return props;
    }
}