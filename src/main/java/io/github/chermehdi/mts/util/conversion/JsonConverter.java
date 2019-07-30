package io.github.chermehdi.mts.util.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.chermehdi.mts.util.ObjectsUtils;
import io.github.chermehdi.mts.util.validation.Validation;

/**
 * @author chermehdi
 */
public class JsonConverter {

  private ObjectMapper objectMapper;

  public String convert(Object object) {
    Validation.notNull(object);
    return ObjectsUtils.supplyUnchecked(() -> mapper().writeValueAsString(object))
        .get();
  }

  public <T> T parse(String value, Class<T> clazz) {
    Validation.notNull(value);
    Validation.notNull(clazz);
    return ObjectsUtils.supplyUnchecked(() -> mapper().readValue(value.getBytes(), clazz))
        .get();
  }

  private ObjectMapper mapper() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    return objectMapper;
  }

}
