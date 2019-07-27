package io.github.chermehdi.mts.util.validation;

import static io.github.chermehdi.mts.util.validation.Validation.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author chermehdi
 */
class ValidationTest {

  @Test
  void testValidationSubjectCreatedOfNullObject() {
    assertThrows(RuntimeException.class, () -> {
      validate(null);
    });
  }

  @Test
  public void testValidationShouldReturnGenericMessage() {
    var ex = assertThrows(ValidationException.class, () -> {
      validate(1)
          .assureThat(e -> e < 0);
    });
    assertEquals("a validation error occurred", ex.getMessage());
  }

  @Test
  public void testValidationShouldReturnExpectedMessage() {
    final String expectedMessage = "value must be positive";
    var ex = assertThrows(ValidationException.class, () -> {
      validate(1)
          .assureThat(e -> e < 0, expectedMessage);
    });
    assertEquals(expectedMessage, ex.getMessage());
  }

  @Test
  public void testEagerValidation() {
    final String firstMessage = "value must be bigger than one";
    final String secondMessage = "value must be equal to one";
    var ex = assertThrows(ValidationException.class, () -> {
      validate(1)
          .assureThat(e -> e > 1, firstMessage)
          .assureThat(e -> e == 1, secondMessage);
    });

    assertEquals(firstMessage, ex.getMessage());
    assertNotEquals(secondMessage, ex.getMessage());
  }

  @Test
  public void testNoValidationError() {
    String value = "123";
    String validated = validate(value)
        .assureThat(e -> e.equals("123"))
        .assureThat(e -> e instanceof String)
        .get();
    assertSame(value, validated);
  }

  @Test
  public void testNotNullValidation() {
    String value = "123";
    String valueValidated = Validation.notNull(value);
    assertEquals(value, valueValidated);

    assertThrows(ValidationException.class, () -> {
      Validation.notNull(null);
    });
  }
}