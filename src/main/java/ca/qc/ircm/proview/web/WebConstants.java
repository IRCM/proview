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

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.internal.ReflectTools;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Constants for Web.
 */
public class WebConstants {
  public static final Locale ENGLISH = Locale.CANADA;
  public static final Locale FRENCH = Locale.CANADA_FRENCH;
  public static final Locale DEFAULT_LOCALE = ENGLISH;
  public static final String GENERAL_MESSAGES = "VaadinMessages";
  public static final String APPLICATION_NAME = "application.name";
  public static final String TITLE = "title";
  public static final String THEME = "theme";
  public static final String PRIMARY = "primary";
  public static final String COMPONENTS = "components";
  public static final String FIELD_NOTIFICATION = "field.notification";
  public static final String PLACEHOLDER = "placeholder";
  public static final String REQUIRED = "required";
  public static final String REQUIRED_CAPTION = "required.caption";
  public static final String LABEL_WARNING = "warning";
  public static final String INVALID = "invalid";
  public static final String INVALID_NUMBER = "invalidNumber";
  public static final String INVALID_INTEGER = "invalidInteger";
  public static final String INVALID_EMAIL = "invalidEmail";
  public static final String ALREADY_EXISTS = "alreadyExists";
  public static final String OUT_OF_RANGE = "outOfRange";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String ERROR_TEXT = "error-text";
  public static final String BORDER = "border";
  public static final String RIGHT = "right";
  public static final String CANCEL = "cancel";
  public static final String BUTTON_SKIP_ROW = "skip-row";
  public static final String UPLOAD_STATUS = "uploadStatus";
  public static final String OVER_MAXIMUM_SIZE = "overMaximumSize";
  public static final String BANNED = "banned";
  public static final String ALL = "all";
  public static final String SAVE = "save";
  public static final String EDIT = "edit";
  public static final String PRINT = "print";
  public static final String SAVED_SUBMISSIONS = "savedSubmissions";
  public static final String SAVED_SAMPLES = "savedSamples";
  public static final String SAVED_CONTAINERS = "savedContainers";
  public static final String SAVED_SAMPLE_FROM_MULTIPLE_USERS = "savedSamplesFromMultipleUsers";
  public static final Method VALUE_CHANGE_LISTENER_METHOD =
      ReflectTools.findMethod(ValueChangeListener.class, "valueChanged", ValueChangeEvent.class);

  /**
   * Returns all valid locales for program.
   *
   * @return all valid locales for program
   */
  public static List<Locale> getLocales() {
    List<Locale> locales = new ArrayList<>();
    locales.add(ENGLISH);
    locales.add(FRENCH);
    return locales;
  }
}
