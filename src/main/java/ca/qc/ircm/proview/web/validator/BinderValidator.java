package ca.qc.ircm.proview.web.validator;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates binder.
 */
public interface BinderValidator {
  /**
   * Validates binder.
   *
   * @param binder
   *          binder
   * @return true if validation succeeded, false otherwise
   */
  public default boolean validate(Binder<?> binder) {
    BinderValidationStatus<?> status = binder.validate();
    logValidation(status);
    return status.isOk();
  }

  /**
   * Logs binder's validation errors.
   *
   * @param status
   *          binder's validation status
   */
  public default void logValidation(BinderValidationStatus<?> status) {
    Logger logger = LoggerFactory.getLogger(getClass());
    status.getFieldValidationErrors().forEach(error -> {
      logger.trace("Validation error {} for field {} with value {} in binder {}",
          error.getMessage(), ((Component) error.getField()).getStyleName(),
          error.getField().getValue(), status.getBinder().getBean());
    });
    status.getBeanValidationErrors().forEach(error -> {
      logger.trace("Validation error {} in binder {}", error.getErrorMessage(),
          status.getBinder().getBean());
    });
  }
}
