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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.user.web.PasswordsForm.CLASS_NAME;
import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORDS_NOT_MATCH;
import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD_CONFIRM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link PasswordsForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PasswordsFormTest extends SpringUIUnitTest {
  private PasswordsForm form;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(PasswordsForm.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private String password = "test_password";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    navigate(ProfileView.class);
    form = $(PasswordsForm.class).first();
  }

  private void fillForm() {
    form.password.setValue(password);
    form.passwordConfirm.setValue(password);
  }

  @Test
  public void styles() {
    assertTrue(form.hasClassName(CLASS_NAME));
    assertTrue(form.password.hasClassName(PASSWORD));
    assertTrue(form.passwordConfirm.hasClassName(PASSWORD_CONFIRM));
  }

  @Test
  public void labels() {
    assertEquals(resources.message(PASSWORD), form.password.getLabel());
    assertEquals(resources.message(PASSWORD_CONFIRM), form.passwordConfirm.getLabel());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(PasswordsForm.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(PASSWORD), form.password.getLabel());
    assertEquals(resources.message(PASSWORD_CONFIRM), form.passwordConfirm.getLabel());
  }

  @Test
  public void getPassword() {
    fillForm();

    assertEquals(password, form.getPassword());
  }

  @Test
  public void getPassword_Empty() {
    form.password.setValue("");
    form.passwordConfirm.setValue("");

    assertNull(form.getPassword());
  }

  @Test
  public void required_Default() {
    assertFalse(form.isRequired());
    assertFalse(form.password.isRequiredIndicatorVisible());
    assertFalse(form.passwordConfirm.isRequiredIndicatorVisible());
  }

  @Test
  public void required_False() {
    form.setRequired(true);
    form.setRequired(false);
    assertFalse(form.isRequired());
    assertFalse(form.password.isRequiredIndicatorVisible());
    assertFalse(form.passwordConfirm.isRequiredIndicatorVisible());
  }

  @Test
  public void required_True() {
    form.setRequired(true);
    assertTrue(form.isRequired());
    assertTrue(form.password.isRequiredIndicatorVisible());
    assertTrue(form.passwordConfirm.isRequiredIndicatorVisible());
  }

  @Test
  public void validate_PasswordEmpty() {
    fillForm();
    form.password.setValue("");

    BinderValidationStatus<Passwords> status = form.validate();

    assertTrue(status.isOk());
  }

  @Test
  public void validate_RequiredPasswordEmpty() {
    form.setRequired(true);
    fillForm();
    form.password.setValue("");

    BinderValidationStatus<Passwords> status = form.validate();

    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.password);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void validate_PasswordsNotMatch() {
    fillForm();
    form.password.setValue("test");
    form.passwordConfirm.setValue("test2");

    BinderValidationStatus<Passwords> status = form.validate();

    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.password);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(PASSWORDS_NOT_MATCH)), error.getMessage());
  }

  @Test
  public void validate_PasswordConfirmEmpty() {
    fillForm();
    form.passwordConfirm.setValue("");

    BinderValidationStatus<Passwords> status = form.validate();

    assertTrue(status.isOk());
  }

  @Test
  public void validate_RequiredPasswordConfirmEmpty() {
    form.setRequired(true);
    fillForm();
    form.passwordConfirm.setValue("");

    BinderValidationStatus<Passwords> status = form.validate();

    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.passwordConfirm);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_PasswordEmpty() {
    fillForm();
    form.password.setValue("");

    boolean valid = form.isValid();

    assertTrue(valid);
  }

  @Test
  public void isValid_RequiredPasswordEmpty() {
    form.setRequired(true);
    fillForm();
    form.password.setValue("");

    boolean valid = form.isValid();

    assertFalse(valid);
  }

  @Test
  public void isValid_PasswordsNotMatch() {
    fillForm();
    form.password.setValue("test");
    form.passwordConfirm.setValue("test2");

    boolean valid = form.isValid();

    assertFalse(valid);
  }

  @Test
  public void isValid_PasswordConfirmEmpty() {
    fillForm();
    form.passwordConfirm.setValue("");

    boolean valid = form.isValid();

    assertTrue(valid);
  }

  @Test
  public void isValid_RequiredPasswordConfirmEmpty() {
    form.setRequired(true);
    fillForm();
    form.passwordConfirm.setValue("");

    boolean valid = form.isValid();

    assertFalse(valid);
  }
}
