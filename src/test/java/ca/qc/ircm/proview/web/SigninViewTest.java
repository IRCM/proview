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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.HASHED_PASSWORD;
import static ca.qc.ircm.proview.web.SigninView.ADDITIONAL_INFORMATION;
import static ca.qc.ircm.proview.web.SigninView.DESCRIPTION;
import static ca.qc.ircm.proview.web.SigninView.FAIL;
import static ca.qc.ircm.proview.web.SigninView.FORGOT_PASSWORD;
import static ca.qc.ircm.proview.web.SigninView.FORM_TITLE;
import static ca.qc.ircm.proview.web.SigninView.HEADER;
import static ca.qc.ircm.proview.web.SigninView.ID;
import static ca.qc.ircm.proview.web.SigninView.SIGNIN;
import static ca.qc.ircm.proview.web.SigninView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ForgotPasswordView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;

/**
 * Tests for {@link SigninView}.
 */
@ServiceTestAnnotations
@WithAnonymousUser
public class SigninViewTest extends SpringUIUnitTest {
  private SigninView view;
  @Autowired
  private SecurityConfiguration configuration;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Mock
  private AfterNavigationEvent afterNavigationEvent;
  @Mock
  private Location location;
  @Mock
  private QueryParameters queryParameters;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SigninView.class, locale);
  private AppResources userResources = new AppResources(User.class, locale);
  private AppResources generalResources = new AppResources(Constants.class, locale);
  private Map<String, List<String>> parameters = new HashMap<>();

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    when(location.getQueryParameters()).thenReturn(queryParameters);
    when(queryParameters.getParameters()).thenReturn(parameters);
    UI.getCurrent().setLocale(locale);
    view = navigate(SigninView.class);
  }

  @Test
  public void init() {
    assertEquals(VIEW_NAME, view.getAction());
    assertTrue(view.isOpened());
    assertTrue(view.isForgotPasswordButtonVisible());
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(resources.message(HEADER), view.i18n.getHeader().getTitle());
    assertEquals(resources.message(DESCRIPTION), view.i18n.getHeader().getDescription());
    assertEquals(resources.message(ADDITIONAL_INFORMATION), view.i18n.getAdditionalInformation());
    assertEquals(resources.message(FORM_TITLE), view.i18n.getForm().getTitle());
    assertEquals(userResources.message(EMAIL), view.i18n.getForm().getUsername());
    assertEquals(userResources.message(HASHED_PASSWORD), view.i18n.getForm().getPassword());
    assertEquals(resources.message(SIGNIN), view.i18n.getForm().getSubmit());
    assertEquals(resources.message(FORGOT_PASSWORD), view.i18n.getForm().getForgotPassword());
    assertEquals(resources.message(property(FAIL, TITLE)), view.i18n.getErrorMessage().getTitle());
    assertEquals(resources.message(FAIL), view.i18n.getErrorMessage().getMessage());
    assertFalse(view.isError());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SigninView.class, locale);
    final AppResources userResources = new AppResources(User.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER), view.i18n.getHeader().getTitle());
    assertEquals(resources.message(DESCRIPTION), view.i18n.getHeader().getDescription());
    assertEquals(resources.message(ADDITIONAL_INFORMATION), view.i18n.getAdditionalInformation());
    assertEquals(resources.message(FORM_TITLE), view.i18n.getForm().getTitle());
    assertEquals(userResources.message(EMAIL), view.i18n.getForm().getUsername());
    assertEquals(userResources.message(HASHED_PASSWORD), view.i18n.getForm().getPassword());
    assertEquals(resources.message(SIGNIN), view.i18n.getForm().getSubmit());
    assertEquals(resources.message(FORGOT_PASSWORD), view.i18n.getForm().getForgotPassword());
    assertEquals(resources.message(property(FAIL, TITLE)), view.i18n.getErrorMessage().getTitle());
    assertEquals(resources.message(FAIL), view.i18n.getErrorMessage().getMessage());
    assertFalse(view.isError());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, generalResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void forgotPassword() {
    view.fireForgotPasswordEvent();
    assertTrue($(ForgotPasswordView.class).exists());
  }
}
