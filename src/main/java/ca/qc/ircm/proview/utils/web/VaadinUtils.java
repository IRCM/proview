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

package ca.qc.ircm.proview.utils.web;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Field;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Utilities for Vaadin.
 */
@Component
@UIScope
public class VaadinUtils implements Serializable {
  private static final long serialVersionUID = -4443205567496277892L;

  /**
   * Returns first non-empty field message found in commit exception.
   *
   * @param exception
   *          commit exception
   * @param locale
   *          locale of generic default message to return if no field message is found
   * @return first non-empty field message found in commit exception
   */
  public String getFieldMessage(CommitException exception, Locale locale) {
    MessageResource resources = new MessageResource(VaadinUtils.class, locale);
    return getFieldMessage(exception, resources.message("defaultMessage"));
  }

  /**
   * Returns first non-empty field message found in commit exception.
   *
   * @param exception
   *          commit exception
   * @param defaultMessage
   *          default message to return if no field message is found
   * @return first non-empty field message found in commit exception
   */
  public String getFieldMessage(CommitException exception, String defaultMessage) {
    String message = null;
    Map<Field<?>, InvalidValueException> fieldExceptions = exception.getInvalidFields();
    for (InvalidValueException fieldException : fieldExceptions.values()) {
      message = message == null || message.isEmpty() ? fieldException.getMessage() : message;
    }
    if (message == null || message.isEmpty()) {
      message = defaultMessage;
    }
    return message;
  }

  public ServletContext getServletContext() {
    return VaadinServlet.getCurrent().getServletContext();
  }

  public String getUrl(String viewName) {
    return getServletContext().getContextPath() + "/#!" + viewName;
  }
}
