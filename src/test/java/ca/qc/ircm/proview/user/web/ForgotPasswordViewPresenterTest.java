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

import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.INVALID_FORGOT_PASSWORD;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.PASSWORD;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.PASSWORD_PANEL;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.SAVE;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.TITLE;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.PasswordField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ForgotPasswordViewPresenterTest {
  private ForgotPasswordViewPresenter presenter;
  @Mock
  private ForgotPasswordView view;
  @Mock
  private ForgotPasswordService forgotPasswordService;
  @Mock
  private ForgotPassword forgotPassword;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ForgotPasswordView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String password = "my_new_password";
  private long id = 12345;
  private int confirmNumber = 348906173;
  private String parameters;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ForgotPasswordViewPresenter(forgotPasswordService, applicationName);
    view.headerLabel = new Label();
    view.passwordPanel = new Panel();
    view.passwordField = new PasswordField();
    view.confirmPasswordField = new PasswordField();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(forgotPasswordService.get(any(), any())).thenReturn(forgotPassword);
    parameters = id + "/" + confirmNumber;
    presenter.init(view);
  }

  private void setFields() {
    view.passwordField.setValue(password);
    view.confirmPasswordField.setValue(password);
  }

  private String errorMessage(String message) {
    return new CompositeErrorMessage(new UserError(message)).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
    assertTrue(view.passwordPanel.getStyleName().contains(PASSWORD_PANEL));
    assertTrue(view.passwordField.getStyleName().contains(PASSWORD));
    assertTrue(view.confirmPasswordField.getStyleName().contains(CONFIRM_PASSWORD));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(PASSWORD_PANEL), view.passwordPanel.getCaption());
    assertEquals(resources.message(PASSWORD), view.passwordField.getCaption());
    assertEquals(resources.message(CONFIRM_PASSWORD), view.confirmPasswordField.getCaption());
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
  }

  @Test
  public void nullRepresentation() {
    assertEquals("", view.passwordField.getNullRepresentation());
    assertEquals("", view.confirmPasswordField.getNullRepresentation());
  }

  @Test
  public void required() {
    assertTrue(view.passwordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.passwordField.getRequiredError());
    assertTrue(view.confirmPasswordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.confirmPasswordField.getRequiredError());
  }

  @Test
  public void save_MissingPassword() {
    presenter.enter(parameters);
    setFields();
    view.passwordField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).updatePassword(any(), any());
  }

  @Test
  public void save_MissingConfirmPassword() {
    presenter.enter(parameters);
    setFields();
    view.confirmPasswordField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.confirmPasswordField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).updatePassword(any(), any());
  }

  @Test
  public void save_PasswordsDontMatch() {
    presenter.enter(parameters);
    setFields();
    view.confirmPasswordField.setValue(password + "2");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(PASSWORD + ".notMatch")),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).updatePassword(any(), any());
  }

  @Test
  public void save() {
    presenter.enter(parameters);
    setFields();

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(forgotPasswordService).updatePassword(forgotPassword, password);
    verify(view).showTrayNotification(resources.message("save.done"));
    verify(view).navigateTo(MainView.VIEW_NAME);
  }

  @Test
  public void enter() {
    presenter.enter(parameters);

    verify(forgotPasswordService).get(id, confirmNumber);
  }

  @Test
  public void enter_MissingConfirmNumber() {
    parameters = String.valueOf(id);

    presenter.enter(parameters);

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
    setFields();
    view.saveButton.click();
    verify(view, times(2)).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
  }

  @Test
  public void enter_ExtraParameter() {
    parameters += "/" + 123;

    presenter.enter(parameters);

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
    setFields();
    view.saveButton.click();
    verify(view, times(2)).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
  }

  @Test
  public void enter_InvalidId() {
    parameters = id + "a/" + confirmNumber;

    presenter.enter(parameters);

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
    setFields();
    view.saveButton.click();
    verify(view, times(2)).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
  }

  @Test
  public void enter_InvalidConfirmNumber() {
    parameters = id + "/a" + confirmNumber;

    presenter.enter(parameters);

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
    setFields();
    view.saveButton.click();
    verify(view, times(2)).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
  }

  @Test
  public void enter_NullForgorPassword() {
    when(forgotPasswordService.get(any(), any())).thenReturn(null);

    presenter.enter(parameters);

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
    setFields();
    view.saveButton.click();
    verify(view, times(2)).showError(stringCaptor.capture());
    assertEquals(resources.message(INVALID_FORGOT_PASSWORD), stringCaptor.getValue());
  }
}
