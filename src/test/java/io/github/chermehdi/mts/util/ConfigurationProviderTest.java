package io.github.chermehdi.mts.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author chermehdi
 */
class ConfigurationProviderTest {

  @Test
  public void testCreateConfigurationProvider() {
    var configurationProvider = new ConfigurationProvider();
  }

  @Test
  public void testCreateEmptyConfigurationObject() {
    var configuration = new ConfigurationProvider(false).get();
    assertNotNull(configuration);
    assertEquals(0, configuration.length());
  }

  @Test
  public void testCannotCallGetOnSameConfigurationProviderInstance() {
    var configurationProvider = new ConfigurationProvider(false);
    // first time
    var configuration = configurationProvider.get();
    assertThrows(IllegalStateException.class, () -> {
      configurationProvider.get();
    });
  }

  @Test
  public void testContainsLitteralKeyValuePairs() {
    var configuration = new ConfigurationProvider(false)
        .addProperty("foo", "bar")
        .addProperty("baz", "doo")
        .get();
    assertEquals(2, configuration.length());
    assertEquals("bar", configuration.getProperty("foo"));
    assertEquals("doo", configuration.getProperty("baz"));
  }

  @Test
  public void testLoadOtherConfigurationFiles() {
    var configuration = new ConfigurationProvider(false)
        .addFile("config-1.properties")
        .get();
    assertEquals(1, configuration.length());
    assertEquals("123", configuration.getProperty("property.name"));
  }
}