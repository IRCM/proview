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

package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * Validates that value is not empty, but only if component is enabled.
 */
public class RequiredIfEnabledValidator<T> implements Validator<T> {
  private static final long serialVersionUID = 8981491477845535933L;
  private String errorMessage;

  public RequiredIfEnabledValidator(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public ValidationResult apply(T value, ValueContext context) {
    Component component = context.getComponent().get();
    @SuppressWarnings("unchecked")
    HasValue<?, T> componentWithValue = (HasValue<?, T>) component;
    if (((HasEnabled) component).isEnabled()) {
      T empty = componentWithValue.getEmptyValue();
      if (empty == null ? value == null : empty.equals(value)) {
        return ValidationResult.error(errorMessage);
      }
    }
    return ValidationResult.ok();
  }
}
