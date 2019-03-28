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

import static ca.qc.ircm.proview.web.MenuPresenter.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.MenuPresenter.CONTACT;
import static ca.qc.ircm.proview.web.MenuPresenter.CONTROL;
import static ca.qc.ircm.proview.web.MenuPresenter.GUIDELINES;
import static ca.qc.ircm.proview.web.MenuPresenter.HOME;
import static ca.qc.ircm.proview.web.MenuPresenter.PLATE;
import static ca.qc.ircm.proview.web.MenuPresenter.PROFILE;
import static ca.qc.ircm.proview.web.MenuPresenter.SIGNIN;
import static ca.qc.ircm.proview.web.MenuPresenter.SIGNOUT;
import static ca.qc.ircm.proview.web.MenuPresenter.STOP_SIGN_AS;
import static ca.qc.ircm.proview.web.MenuPresenter.SUBMISSION;
import static ca.qc.ircm.proview.web.MenuPresenter.USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.plate.web.PlatesView;
import ca.qc.ircm.proview.sample.web.ControlView;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.proview.user.web.SignoutFilter;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import java.util.Locale;
import java.util.Optional;
import javax.servlet.ServletContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class MenuPresenterTest {
  private MenuPresenter presenter;
  @Mock
  private Menu view;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private MainUi ui;
  @Mock
  private VaadinSession session;
  @Mock
  private Page page;
  @Mock
  private ServletContext servletContext;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(Menu.class, locale);
  private String contextPath = "/";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new MenuPresenter(authorizationService, authenticationService);
    view.menu = new MenuBar();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getUI()).thenReturn(ui);
    when(ui.getSession()).thenReturn(session);
    when(ui.getPage()).thenReturn(page);
    when(ui.getServletContext()).thenReturn(servletContext);
    when(servletContext.getContextPath()).thenReturn(contextPath);
  }

  private MenuItem item(String classname) {
    return view.menu.getItems().stream().map(child -> item(child, classname))
        .filter(opt -> opt.isPresent()).findFirst().get().get();
  }

  private Optional<MenuItem> item(MenuItem item, String classname) {
    if (item.getStyleName().contains(classname)) {
      return Optional.of(item);
    }
    if (item.getChildren() != null) {
      return item.getChildren().stream().map(child -> item(child, classname))
          .filter(opt -> opt.isPresent()).findFirst().orElse(Optional.empty());
    }
    return Optional.empty();
  }

  @Test
  public void menu() throws Throwable {
    presenter.init(view);

    assertTrue(view.menu.getItems().get(0).getStyleName().contains(HOME));
    assertTrue(view.menu.getItems().get(1).getStyleName().contains(SUBMISSION));
    assertTrue(view.menu.getItems().get(2).getStyleName().contains(CONTROL));
    assertTrue(view.menu.getItems().get(3).getStyleName().contains(PLATE));
    assertTrue(view.menu.getItems().get(4).getStyleName().contains(PROFILE));
    assertTrue(view.menu.getItems().get(5).getStyleName().contains(SIGNOUT));
    assertTrue(view.menu.getItems().get(6).getStyleName().contains(CHANGE_LANGUAGE));
    assertTrue(view.menu.getItems().get(7).getStyleName().contains(USERS));
    assertTrue(view.menu.getItems().get(8).getStyleName().contains(CONTACT));
    assertTrue(view.menu.getItems().get(9).getStyleName().contains(GUIDELINES));
    assertTrue(view.menu.getItems().get(10).getStyleName().contains(SIGNIN));
    assertTrue(view.menu.getItems().get(11).getStyleName().contains(STOP_SIGN_AS));
  }

  @Test
  public void visibility_Anonymous() throws Throwable {
    presenter.init(view);

    assertTrue(item(HOME).isVisible());
    assertFalse(item(SUBMISSION).isVisible());
    assertFalse(item(CONTROL).isVisible());
    assertFalse(item(PLATE).isVisible());
    assertFalse(item(PROFILE).isVisible());
    assertFalse(item(SIGNOUT).isVisible());
    assertTrue(item(CHANGE_LANGUAGE).isVisible());
    assertFalse(item(USERS).isVisible());
    assertTrue(item(CONTACT).isVisible());
    assertFalse(item(GUIDELINES).isVisible());
    assertTrue(item(SIGNIN).isVisible());
    assertFalse(item(STOP_SIGN_AS).isVisible());
  }

  @Test
  public void visibility_User() throws Throwable {
    when(authorizationService.isUser()).thenReturn(true);
    when(authorizationService.hasUserRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(item(HOME).isVisible());
    assertTrue(item(SUBMISSION).isVisible());
    assertFalse(item(CONTROL).isVisible());
    assertFalse(item(PLATE).isVisible());
    assertTrue(item(PROFILE).isVisible());
    assertTrue(item(SIGNOUT).isVisible());
    assertTrue(item(CHANGE_LANGUAGE).isVisible());
    assertFalse(item(USERS).isVisible());
    assertTrue(item(CONTACT).isVisible());
    assertTrue(item(GUIDELINES).isVisible());
    assertFalse(item(SIGNIN).isVisible());
    assertFalse(item(STOP_SIGN_AS).isVisible());
  }

  @Test
  public void visibility_Manager() throws Throwable {
    when(authorizationService.isUser()).thenReturn(true);
    when(authorizationService.hasUserRole()).thenReturn(true);
    when(authorizationService.hasManagerRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(item(HOME).isVisible());
    assertTrue(item(SUBMISSION).isVisible());
    assertFalse(item(CONTROL).isVisible());
    assertFalse(item(PLATE).isVisible());
    assertTrue(item(PROFILE).isVisible());
    assertTrue(item(SIGNOUT).isVisible());
    assertTrue(item(CHANGE_LANGUAGE).isVisible());
    assertTrue(item(USERS).isVisible());
    assertTrue(item(CONTACT).isVisible());
    assertTrue(item(GUIDELINES).isVisible());
    assertFalse(item(SIGNIN).isVisible());
    assertFalse(item(STOP_SIGN_AS).isVisible());
  }

  @Test
  public void visibility_Admin() throws Throwable {
    when(authorizationService.isUser()).thenReturn(true);
    when(authorizationService.hasUserRole()).thenReturn(true);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(item(HOME).isVisible());
    assertTrue(item(SUBMISSION).isVisible());
    assertTrue(item(CONTROL).isVisible());
    assertTrue(item(PLATE).isVisible());
    assertTrue(item(PROFILE).isVisible());
    assertTrue(item(SIGNOUT).isVisible());
    assertTrue(item(CHANGE_LANGUAGE).isVisible());
    assertTrue(item(USERS).isVisible());
    assertTrue(item(CONTACT).isVisible());
    assertTrue(item(GUIDELINES).isVisible());
    assertFalse(item(SIGNIN).isVisible());
    assertFalse(item(STOP_SIGN_AS).isVisible());
  }

  @Test
  public void visibility_SignedAs() throws Throwable {
    when(authorizationService.isUser()).thenReturn(true);
    when(authorizationService.hasUserRole()).thenReturn(true);
    when(authorizationService.isRunAs()).thenReturn(true);
    presenter.init(view);

    assertTrue(item(HOME).isVisible());
    assertTrue(item(SUBMISSION).isVisible());
    assertFalse(item(CONTROL).isVisible());
    assertFalse(item(PLATE).isVisible());
    assertTrue(item(PROFILE).isVisible());
    assertTrue(item(SIGNOUT).isVisible());
    assertTrue(item(CHANGE_LANGUAGE).isVisible());
    assertFalse(item(USERS).isVisible());
    assertTrue(item(CONTACT).isVisible());
    assertTrue(item(GUIDELINES).isVisible());
    assertFalse(item(SIGNIN).isVisible());
    assertTrue(item(STOP_SIGN_AS).isVisible());
  }

  @Test
  public void captions() throws Throwable {
    presenter.init(view);

    assertEquals(resources.message(HOME), item(HOME).getText());
    assertEquals(resources.message(SUBMISSION), item(SUBMISSION).getText());
    assertEquals(resources.message(CONTROL), item(CONTROL).getText());
    assertEquals(resources.message(PLATE), item(PLATE).getText());
    assertEquals(resources.message(PROFILE), item(PROFILE).getText());
    assertEquals(resources.message(SIGNOUT), item(SIGNOUT).getText());
    assertEquals(resources.message(CHANGE_LANGUAGE), item(CHANGE_LANGUAGE).getText());
    assertEquals(resources.message(USERS), item(USERS).getText());
    assertEquals(resources.message(CONTACT), item(CONTACT).getText());
    assertEquals(resources.message(GUIDELINES), item(GUIDELINES).getText());
    assertEquals(resources.message(SIGNIN), item(SIGNIN).getText());
    assertEquals(resources.message(STOP_SIGN_AS), item(STOP_SIGN_AS).getText());
  }

  @Test
  public void home() throws Throwable {
    presenter.init(view);

    item(HOME).getCommand().menuSelected(item(HOME));

    verify(view).navigateTo(MainView.VIEW_NAME);
  }

  @Test
  public void submission() throws Throwable {
    presenter.init(view);

    item(SUBMISSION).getCommand().menuSelected(item(SUBMISSION));

    verify(view).navigateTo(SubmissionView.VIEW_NAME);
  }

  @Test
  public void control() throws Throwable {
    presenter.init(view);

    item(CONTROL).getCommand().menuSelected(item(CONTROL));

    verify(view).navigateTo(ControlView.VIEW_NAME);
  }

  @Test
  public void plate() throws Throwable {
    presenter.init(view);

    item(PLATE).getCommand().menuSelected(item(PLATE));

    verify(view).navigateTo(PlatesView.VIEW_NAME);
  }

  @Test
  public void profile() throws Throwable {
    presenter.init(view);

    item(PROFILE).getCommand().menuSelected(item(PROFILE));

    verify(view).navigateTo(UserView.VIEW_NAME);
  }

  @Test
  public void signout() throws Throwable {
    presenter.init(view);

    item(SIGNOUT).getCommand().menuSelected(item(SIGNOUT));

    verify(page).setLocation(contextPath + SignoutFilter.SIGNOUT_URL);
  }

  @Test
  public void changeLanguage_FrenchToEnglish() throws Throwable {
    presenter.init(view);

    item(CHANGE_LANGUAGE).getCommand().menuSelected(item(CHANGE_LANGUAGE));

    verify(session).setLocale(Locale.ENGLISH);
    verify(ui).setLocale(Locale.ENGLISH);
    verify(page).reload();
  }

  @Test
  public void changeLanguage_EnglishToFrench() throws Throwable {
    locale = Locale.ENGLISH;
    when(view.getLocale()).thenReturn(locale);
    presenter.init(view);

    item(CHANGE_LANGUAGE).getCommand().menuSelected(item(CHANGE_LANGUAGE));

    verify(session).setLocale(Locale.FRENCH);
    verify(ui).setLocale(Locale.FRENCH);
    verify(page).reload();
  }

  @Test
  public void users() throws Throwable {
    presenter.init(view);

    item(USERS).getCommand().menuSelected(item(USERS));

    verify(view).navigateTo(UsersView.VIEW_NAME);
  }

  @Test
  public void contact() throws Throwable {
    presenter.init(view);

    item(CONTACT).getCommand().menuSelected(item(CONTACT));

    verify(view).navigateTo(ContactView.VIEW_NAME);
  }

  @Test
  public void guidelinesMenu() throws Throwable {
    presenter.init(view);

    item(GUIDELINES).getCommand().menuSelected(item(GUIDELINES));

    verify(view).navigateTo(GuidelinesView.VIEW_NAME);
  }

  @Test
  public void signinMenu() throws Throwable {
    presenter.init(view);

    item(SIGNIN).getCommand().menuSelected(item(SIGNIN));

    verify(view).navigateTo(SigninView.VIEW_NAME);
  }

  @Test
  public void stopSignas() throws Throwable {
    presenter.init(view);

    item(STOP_SIGN_AS).getCommand().menuSelected(item(STOP_SIGN_AS));

    verify(authenticationService).stopRunAs();
    verify(view).navigateTo(MainView.VIEW_NAME);
  }
}
