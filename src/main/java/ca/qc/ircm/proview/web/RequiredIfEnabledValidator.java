package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import java.io.Serial;
import java.util.Objects;
import org.springframework.lang.Nullable;

/**
 * Validates that value is not empty, but only if component is enabled.
 */
public class RequiredIfEnabledValidator<T> implements Validator<T> {
  @Serial
  private static final long serialVersionUID = 8981491477845535933L;
  private String errorMessage;

  public RequiredIfEnabledValidator(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public ValidationResult apply(@Nullable T value, ValueContext context) {
    Component component = context.getComponent().orElseThrow();
    @SuppressWarnings("unchecked")
    HasValue<?, T> componentWithValue = (HasValue<?, T>) component;
    if (((HasEnabled) component).isEnabled()) {
      T empty = componentWithValue.getEmptyValue();
      if (Objects.equals(empty, value)) {
        return ValidationResult.error(errorMessage);
      }
    }
    return ValidationResult.ok();
  }
}
