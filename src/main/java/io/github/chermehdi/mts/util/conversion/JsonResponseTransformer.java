package io.github.chermehdi.mts.util.conversion;

import spark.ResponseTransformer;

/**
 * @author chermehdi
 */
public class JsonResponseTransformer implements ResponseTransformer {

  private JsonConverter converter;

  @Override
  public String render(Object model) throws Exception {
    return converter().convert(model);
  }

  private JsonConverter converter() {
    return converter == null ? (converter = new JsonConverter()) : converter;
  }
}
