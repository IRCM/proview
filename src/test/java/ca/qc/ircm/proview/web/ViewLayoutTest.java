/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.HOME;
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.TAB;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.WebSecurityConfiguration;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ViewLayoutTest extends AbstractViewTestCase {
  private ViewLayout view;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private AfterNavigationEvent afterNavigationEvent;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ViewLayout.class, locale);
  private User user = new User(1L, "myuser");

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new ViewLayout(authorizationService);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    view.init();
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(styleName(HOME, TAB), view.home.getId().orElse(""));
    assertEquals(styleName(USERS, TAB), view.users.getId().orElse(""));
    assertEquals(styleName(EXIT_SWITCH_USER, TAB), view.exitSwitchUser.getId().orElse(""));
    assertEquals(styleName(SIGNOUT, TAB), view.signout.getId().orElse(""));
    assertEquals(styleName(CHANGE_LANGUAGE, TAB), view.changeLanguage.getId().orElse(""));
    assertEquals(styleName(CONTACT, TAB), view.contact.getId().orElse(""));
    assertEquals(styleName(GUIDELINES, TAB), view.guidelines.getId().orElse(""));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HOME), view.home.getLabel());
    assertEquals(resources.message(USERS), view.users.getLabel());
    assertEquals(resources.message(EXIT_SWITCH_USER), view.exitSwitchUser.getLabel());
    assertEquals(resources.message(SIGNOUT), view.signout.getLabel());
    assertEquals(resources.message(CHANGE_LANGUAGE), view.changeLanguage.getLabel());
    assertEquals(resources.message(CONTACT), view.contact.getLabel());
    assertEquals(resources.message(GUIDELINES), view.guidelines.getLabel());
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = Locale.FRENCH;
    final MessageResource resources = new MessageResource(ViewLayout.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HOME), view.home.getLabel());
    assertEquals(resources.message(USERS), view.users.getLabel());
    assertEquals(resources.message(EXIT_SWITCH_USER), view.exitSwitchUser.getLabel());
    assertEquals(resources.message(SIGNOUT), view.signout.getLabel());
    assertEquals(resources.message(CHANGE_LANGUAGE), view.changeLanguage.getLabel());
    assertEquals(resources.message(CONTACT), view.contact.getLabel());
    assertEquals(resources.message(GUIDELINES), view.guidelines.getLabel());
  }

  @Test
  public void tabs() {
    view.init();
    assertTrue(view.home.isVisible());
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
  }

  @Test
  public void tabs_SwitchedUser() {
    when(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR))
        .thenReturn(true);
    view.init();
    assertTrue(view.home.isVisible());
    assertTrue(view.users.isVisible());
    assertTrue(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
  }

  @Test
  public void tabs_SelectHome() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.home);

    verify(ui).navigate(MainView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectHomeNoChange() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.home);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectUsers() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.users);

    verify(ui).navigate(UsersView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectUsersNoChange() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.users);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectContact() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.contact);

    verify(ui).navigate(ContactView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectContactNoChange() {
    Location location = new Location(ContactView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.contact);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectGuidelines() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.guidelines);

    verify(ui).navigate(GuidelinesView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectGuidelinesNoChange() {
    Location location = new Location(GuidelinesView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.guidelines);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectExitSwitchUser() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.exitSwitchUser);

    verify(ui, never()).navigate(any(String.class));
    verify(page)
        .executeJs("location.assign('" + WebSecurityConfiguration.SWITCH_USER_EXIT_URL + "')");
  }

  @Test
  public void tabs_SelectSignout() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.signout);

    verify(ui, never()).navigate(any(String.class));
    verify(page).executeJs("location.assign('" + WebSecurityConfiguration.SIGNOUT_URL + "')");
  }

  @Test
  public void tabs_ChangeLanguage_ToFrench() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(Locale.FRENCH);
    assertEquals(view.home, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToEnglish() {
    when(ui.getLocale()).thenReturn(Locale.FRENCH);
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(Locale.ENGLISH);
    assertEquals(view.home, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToFrenchFromUsers() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(Locale.FRENCH);
    assertEquals(view.users, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Home() {
    Location location = new Location(MainView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.home, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Users() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.users, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Contact() {
    Location location = new Location(ContactView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.contact, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Guidelines() {
    Location location = new Location(GuidelinesView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.guidelines, view.tabs.getSelectedTab());
  }

  public static class ViewTest {
  }
}
