package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for {@link RequiredIfEnabledValidator}.
 */
@NonTransactionalTestAnnotations
public class RequiredIfEnabledValidatorTest {

  private String errorMessage = "error message";
  @Mock
  private ValueContext context;

  @Test
  public void apply_Null() {
    RequiredIfEnabledValidator<String> validator = new RequiredIfEnabledValidator<>(errorMessage);
    ComboBox<Boolean> field = new ComboBox<>();
    field.setItems(false, true);
    when(context.getComponent()).thenReturn(Optional.of(field));
    ValidationResult result = validator.apply(null, context);
    assertTrue(result.isError());
    assertEquals(errorMessage, result.getErrorMessage());
    assertEquals(Optional.of(ErrorLevel.ERROR), result.getErrorLevel());
  }

  @Test
  public void apply_Empty() {
    RequiredIfEnabledValidator<String> validator = new RequiredIfEnabledValidator<>(errorMessage);
    TextField field = new TextField();
    when(context.getComponent()).thenReturn(Optional.of(field));
    ValidationResult result = validator.apply("", context);
    assertTrue(result.isError());
    assertEquals(errorMessage, result.getErrorMessage());
    assertEquals(Optional.of(ErrorLevel.ERROR), result.getErrorLevel());
  }

  @Test
  public void apply_NotEmpty() {
    RequiredIfEnabledValidator<String> validator = new RequiredIfEnabledValidator<>(errorMessage);
    TextField field = new TextField();
    when(context.getComponent()).thenReturn(Optional.of(field));
    ValidationResult result = validator.apply("not empty", context);
    assertFalse(result.isError());
  }
}
