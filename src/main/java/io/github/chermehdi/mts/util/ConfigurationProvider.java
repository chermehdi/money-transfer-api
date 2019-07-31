package io.github.chermehdi.mts.util;

import io.github.chermehdi.mts.util.validation.Validation;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author chermehdi
 */
public class ConfigurationProvider {

  private final boolean loadFromFile;
  private List<String> configurationFiles = new ArrayList<>();
  private Map<String, String> configuration = new HashMap<>();
  private AtomicBoolean used = new AtomicBoolean(false);

  public static String DEFAULT_CONFIGURATION_FILE_NAME = "config.properties";

  public ConfigurationProvider() {
    this(true);
  }

  public ConfigurationProvider(boolean loadFromFile) {
    this.loadFromFile = loadFromFile;
  }

  public ConfigurationProvider addFile(String file) {
    configurationFiles.add(Validation.notNull(file));
    return this;
  }

  public ConfigurationProvider addProperty(String key, String value) {
    key = Validation.notNull(key);
    value = Validation.notNull(value);
    configuration.put(key, value);
    return this;
  }

  public Configuration get() {
    if (used.get()) {
      throw new IllegalStateException(
          "You can't call get more than once on same instance of ConfigurationProvider");
    }

    if (loadFromFile) {
      configurationFiles.add(DEFAULT_CONFIGURATION_FILE_NAME);
    }

    configurationFiles.stream()
        .map(file -> loadProperties(file))
        .map(supplier -> supplier.get())
        .forEach(props -> addAll(props));

    used.set(true);
    return new Configuration(configuration);
  }

  private void addAll(Properties props) {
    props = Validation.notNull(props, "Properties should not be null");
    props.forEach((key, value) -> configuration.put((String) key, (String) value));
  }

  private Supplier<Properties> loadProperties(String file) {
    InputStream is = Validation.notNull(getClass().getClassLoader().getResourceAsStream(file),
        String.format("Resource %s does not exist in classpath", file));

    return ObjectsUtils.supplyUnchecked(() -> {
      Properties props = new Properties();
      props.load(is);
      return props;
    });
  }

  public static final class Configuration {

    private final Map<String, String> properties;

    Configuration(Map<String, String> properties) {
      this.properties = Collections.unmodifiableMap(Validation.notNull(properties));
    }

    public String getProperty(String propName) {
      return properties.get(propName);
    }

    public int length() {
      return properties.size();
    }

    @Override
    public String toString() {
      return "Configuration{" +
          "properties=" + properties +
          '}';
    }
  }
}
