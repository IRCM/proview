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

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.Rules;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.FieldGroupInvalidValueException;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class VaadinUtilsTest {
  private VaadinUtils vaadinUtils;
  @Rule
  public RuleChain rules = Rules.defaultRules(this);
  private Locale locale = Locale.getDefault();
  private String localeDefaultMessage = "Please fix the errors in red fields";
  private String defaultMessage = "default message";

  @Before
  public void beforeTest() {
    vaadinUtils = new VaadinUtils();
  }

  @Test
  public void getFieldMessage_Locale_DefaultMessage() {
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(localeDefaultMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_FieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_EmptyFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(""));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(localeDefaultMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_NullFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(null));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(localeDefaultMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_2FieldMessages() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    fieldExceptions.put(new TextField(), new InvalidValueException("second message"));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_FirstEmptyFieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(""));
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_FirstNullFieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(null));
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_Locale_NoFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, locale);

    assertEquals(localeDefaultMessage, message);
  }

  @Test
  public void getFieldMessage_DefaultMessage() {
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(defaultMessage, message);
  }

  @Test
  public void getFieldMessage_FieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_EmptyFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(""));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(defaultMessage, message);
  }

  @Test
  public void getFieldMessage_NullFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(null));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(defaultMessage, message);
  }

  @Test
  public void getFieldMessage_2FieldMessages() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    fieldExceptions.put(new TextField(), new InvalidValueException("second message"));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_FirstEmptyFieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(""));
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_FirstNullFieldMessage() {
    String fieldMessage = "error message";
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    fieldExceptions.put(new TextField(), new InvalidValueException(null));
    fieldExceptions.put(new TextField(), new InvalidValueException(fieldMessage));
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(fieldMessage, message);
  }

  @Test
  public void getFieldMessage_NoFieldMessage() {
    Map<Field<?>, InvalidValueException> fieldExceptions = new LinkedHashMap<>();
    FieldGroupInvalidValueException fieldGroupException =
        new FieldGroupInvalidValueException(fieldExceptions);
    String commitMessage = "test";
    CommitException exception = new CommitException(commitMessage, fieldGroupException);

    String message = vaadinUtils.getFieldMessage(exception, defaultMessage);

    assertEquals(defaultMessage, message);
  }
}
