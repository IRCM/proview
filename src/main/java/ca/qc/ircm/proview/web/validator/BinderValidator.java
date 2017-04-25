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
