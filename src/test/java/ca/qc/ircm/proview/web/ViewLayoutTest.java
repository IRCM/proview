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

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.TAB;
import static ca.qc.ircm.proview.web.ViewLayout.TABS;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.WebSecurityConfiguration;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
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
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ViewLayout.class, locale);
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
    assertEquals(TABS, view.tabs.getId().orElse(""));
    assertEquals(styleName(SUBMISSIONS, TAB), view.submissions.getId().orElse(""));
    assertEquals(styleName(PROFILE, TAB), view.profile.getId().orElse(""));
    assertEquals(styleName(USERS, TAB), view.users.getId().orElse(""));
    assertEquals(styleName(EXIT_SWITCH_USER, TAB), view.exitSwitchUser.getId().orElse(""));
    assertEquals(styleName(SIGNOUT, TAB), view.signout.getId().orElse(""));
    assertEquals(styleName(CHANGE_LANGUAGE, TAB), view.changeLanguage.getId().orElse(""));
    assertEquals(styleName(CONTACT, TAB), view.contact.getId().orElse(""));
    assertEquals(styleName(GUIDELINES, TAB), view.guidelines.getId().orElse(""));
    assertEquals(styleName(EDIT, TAB), view.edit.getId().orElse(""));
    assertEquals(styleName(PRINT, TAB), view.print.getId().orElse(""));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SUBMISSIONS), view.submissions.getLabel());
    assertEquals(resources.message(PROFILE), view.profile.getLabel());
    assertEquals(resources.message(USERS), view.users.getLabel());
    assertEquals(resources.message(EXIT_SWITCH_USER), view.exitSwitchUser.getLabel());
    assertEquals(resources.message(SIGNOUT), view.signout.getLabel());
    assertEquals(resources.message(CHANGE_LANGUAGE), view.changeLanguage.getLabel());
    assertEquals(resources.message(CONTACT), view.contact.getLabel());
    assertEquals(resources.message(GUIDELINES), view.guidelines.getLabel());
    assertEquals(resources.message(EDIT), view.edit.getLabel());
    assertEquals(resources.message(PRINT), view.print.getLabel());
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(ViewLayout.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SUBMISSIONS), view.submissions.getLabel());
    assertEquals(resources.message(PROFILE), view.profile.getLabel());
    assertEquals(resources.message(USERS), view.users.getLabel());
    assertEquals(resources.message(EXIT_SWITCH_USER), view.exitSwitchUser.getLabel());
    assertEquals(resources.message(SIGNOUT), view.signout.getLabel());
    assertEquals(resources.message(CHANGE_LANGUAGE), view.changeLanguage.getLabel());
    assertEquals(resources.message(CONTACT), view.contact.getLabel());
    assertEquals(resources.message(GUIDELINES), view.guidelines.getLabel());
    assertEquals(resources.message(EDIT), view.edit.getLabel());
    assertEquals(resources.message(PRINT), view.print.getLabel());
  }

  @Test
  public void tabs() {
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
  }

  @Test
  public void tabs_AllowUsersView() {
    when(authorizationService.isAuthorized(UsersView.class)).thenReturn(true);
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
  }

  @Test
  public void tabs_SwitchedUser() {
    when(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR))
        .thenReturn(true);
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertTrue(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
  }

  @Test
  public void tabs_SelectSubmissions() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.submissions);

    verify(ui).navigate(SubmissionsView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectSubmissionsNoChange() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.submissions);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectProfile() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.profile);

    verify(ui).navigate(ProfileView.VIEW_NAME);
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectProfileNoChange() {
    Location location = new Location(ProfileView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.profile);

    verify(ui, never()).navigate(any(String.class));
    verify(page, never()).executeJs(any());
  }

  @Test
  public void tabs_SelectUsers() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
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
    Location location = new Location(SubmissionsView.VIEW_NAME);
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
    Location location = new Location(SubmissionsView.VIEW_NAME);
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
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.exitSwitchUser);

    verify(ui, never()).navigate(any(String.class));
    verify(page)
        .executeJs("location.assign('" + WebSecurityConfiguration.SWITCH_USER_EXIT_URL + "')");
  }

  @Test
  public void tabs_SelectSignout() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.signout);

    verify(ui, never()).navigate(any(String.class));
    verify(page).executeJs("location.assign('" + WebSecurityConfiguration.SIGNOUT_URL + "')");
  }

  @Test
  public void tabs_ChangeLanguage_ToFrench() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(FRENCH);
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToEnglish() {
    when(ui.getLocale()).thenReturn(FRENCH);
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(ENGLISH);
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToFrenchFromUsers() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(ui, never()).navigate(any(String.class));
    verify(ui).setLocale(FRENCH);
    assertEquals(view.users, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Submissions() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.submissions, view.tabs.getSelectedTab());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_Users() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.users, view.tabs.getSelectedTab());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_Contact() {
    Location location = new Location(ContactView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.contact, view.tabs.getSelectedTab());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_Guidelines() {
    Location location = new Location(GuidelinesView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.guidelines, view.tabs.getSelectedTab());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_Add() {
    Location location = new Location(SubmissionView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.add, view.tabs.getSelectedTab());
    assertTrue(view.add.isVisible());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_AddHideAfterChange() {
    Location location1 = new Location(SubmissionView.VIEW_NAME);
    Location location2 = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location1, location2);

    view.afterNavigation(afterNavigationEvent);
    view.afterNavigation(afterNavigationEvent);

    assertFalse(view.add.isVisible());
  }

  @Test
  public void afterNavigation_Edit() {
    Location location = new Location(SubmissionView.VIEW_NAME + "/12");
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.edit, view.tabs.getSelectedTab());
    assertTrue(view.edit.isVisible());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_EditHideAfterChange() {
    Location location1 = new Location(SubmissionView.VIEW_NAME + "/12");
    Location location2 = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location1, location2);

    view.afterNavigation(afterNavigationEvent);
    view.afterNavigation(afterNavigationEvent);

    assertFalse(view.edit.isVisible());
  }

  @Test
  public void afterNavigation_Print() {
    Location location = new Location(PrintSubmissionView.VIEW_NAME + "/12");
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.print, view.tabs.getSelectedTab());
    assertTrue(view.print.isVisible());
    verify(ui, never()).navigate(any(String.class));
  }

  @Test
  public void afterNavigation_PrintHideAfterChange() {
    Location location1 = new Location(PrintSubmissionView.VIEW_NAME + "/12");
    Location location2 = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location1, location2);

    view.afterNavigation(afterNavigationEvent);
    view.afterNavigation(afterNavigationEvent);

    assertFalse(view.print.isVisible());
  }

  public static class ViewTest {
  }
}
