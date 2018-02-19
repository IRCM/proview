/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web.validator;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
   * Validates values using validator and reports errors to component.
   *
   * @param <V>
   *          type of values
   * @param validator
   *          binder
   * @param component
   *          component where to report errors
   * @param values
   *          values to validate
   * @return true if validation succeeded, false otherwise
   */
  public default <V> boolean validate(Validator<V> validator, AbstractComponent component,
      Collection<V> values) {
    Logger logger = LoggerFactory.getLogger(getClass());
    List<String> errors = new ArrayList<>();
    ValueContext context = new ValueContext(component);
    for (V value : values) {
      ValidationResult result = validator.apply(value, context);
      if (result.isError()) {
        logger.trace("Validation error {} for value {}", result.getErrorMessage(), value);
        errors.add(result.getErrorMessage());
      }
    }
    if (!errors.isEmpty()) {
      List<ErrorMessage> componentErrors = new ArrayList<>();
      if (component.getComponentError() != null) {
        ErrorMessage previous = component.getComponentError();
        if (previous instanceof CompositeErrorMessage) {
          ((CompositeErrorMessage) previous).iterator()
              .forEachRemaining(e -> componentErrors.add(e));
        } else {
          componentErrors.add(previous);
        }
      }
      errors.stream().map(m -> new UserError(m)).forEach(e -> componentErrors.add(e));
      component.setComponentError(new CompositeErrorMessage(componentErrors));
    }
    return errors.isEmpty();
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

  public default void addError(ErrorMessage error, AbstractComponent component) {
    if (component.getComponentError() == null) {
      component.setComponentError(error);
    } else if (component.getComponentError() instanceof CompositeErrorMessage) {
      CompositeErrorMessage componentError = (CompositeErrorMessage) component.getComponentError();
      componentError.addCause(error);
    } else {
      ErrorMessage componentError = component.getComponentError();
      component.setComponentError(new CompositeErrorMessage(componentError, error));
    }
  }
}
