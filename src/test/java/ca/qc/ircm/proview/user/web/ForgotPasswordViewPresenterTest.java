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
import static ca.qc.ircm.proview.Constants.INVALID_EMAIL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SAVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.ForgotPasswordWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * Tests for {@link ForgotPasswordViewPresenter}.
 */
@ServiceTestAnnotations
public class ForgotPasswordViewPresenterTest extends AbstractKaribuTestCase {
  private ForgotPasswordViewPresenter presenter;
  @Mock
  private ForgotPasswordView view;
  @Mock
  private ForgotPasswordService service;
  @Mock
  private UserService userService;
  @Captor
  private ArgumentCaptor<ForgotPasswordWebContext> webContextCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ForgotPasswordView.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private String email = "test@ircm.qc.ca";
  private ForgotPassword forgotPassword;
  private long id = 34925;
  private String confirmNumber = "feafet23ts";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    presenter = new ForgotPasswordViewPresenter(service, userService);
    view.header = new H2();
    view.message = new Div();
    view.email = new TextField();
    view.buttonsLayout = new HorizontalLayout();
    view.save = new Button();
    forgotPassword = new ForgotPassword();
    forgotPassword.setId(id);
    forgotPassword.setConfirmNumber(confirmNumber);
  }

  private void setFields() {
    view.email.setValue(email);
  }

  @Test
  public void init() {
    presenter.init(view);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmailEmtpy() {
    ui.navigate(ForgotPasswordView.class);
    presenter.init(view);
    presenter.localeChange(locale);
    view.email.setValue("");

    presenter.save(locale);

    BinderValidationStatus<User> status = presenter.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(service, never()).insert(any(), any());
    assertCurrentView(ForgotPasswordView.class);
    verify(view, never()).showNotification(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmailInvalid() {
    ui.navigate(ForgotPasswordView.class);
    presenter.init(view);
    presenter.localeChange(locale);
    view.email.setValue("test");

    presenter.save(locale);

    BinderValidationStatus<User> status = presenter.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_EMAIL)), error.getMessage());
    verify(service, never()).insert(any(), any());
    assertCurrentView(ForgotPasswordView.class);
    verify(view, never()).showNotification(any());
  }

  @Test
  public void save_EmailNotExists() {
    ui.navigate(ForgotPasswordView.class);
    presenter.init(view);
    presenter.localeChange(locale);
    setFields();

    presenter.save(locale);

    verify(userService).exists(email);
    verify(service, never()).insert(any(), any());
    assertCurrentView(SigninView.class);
    verify(view).showNotification(resources.message(SAVED, email));
  }

  @Test
  public void save() {
    String viewUrl = "/usefp";
    when(userService.exists(any())).thenReturn(true);
    when(view.getUrl(any())).thenReturn(viewUrl);
    ui.navigate(ForgotPasswordView.class);
    presenter.init(view);
    presenter.localeChange(locale);
    setFields();

    presenter.save(locale);

    verify(userService).exists(email);
    verify(service).insert(eq(email), webContextCaptor.capture());
    ForgotPasswordWebContext webContext = webContextCaptor.getValue();
    String url = webContext.getChangeForgottenPasswordUrl(forgotPassword, locale);
    assertEquals(viewUrl + "/" + id + UseForgotPasswordView.SEPARATOR + confirmNumber, url);
    assertCurrentView(SigninView.class);
    verify(view).showNotification(resources.message(SAVED, email));
  }
}
