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
import static ca.qc.ircm.proview.security.web.WebSecurityConfiguration.SIGNOUT_URL;
import static ca.qc.ircm.proview.security.web.WebSecurityConfiguration.SWITCH_USER_EXIT_URL;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER_FORM;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.HISTORY;
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.TAB;
import static ca.qc.ircm.proview.web.ViewLayout.TABS;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.Location;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Tests for {@link ViewLayout}.
 */
@NonTransactionalTestAnnotations
public class ViewLayoutTest extends AbstractKaribuTestCase {
  private ViewLayout view;
  @Mock
  private AuthenticatedUser authenticatedUser;
  @Mock
  private AfterNavigationListener navigationListener;
  @Mock
  private AfterNavigationEvent afterNavigationEvent;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ViewLayout.class, locale);
  private User user = new User(1L, "myuser");

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    ui.addAfterNavigationListener(navigationListener);
    view = new ViewLayout(authenticatedUser);
    when(authenticatedUser.getCurrentUser()).thenReturn(Optional.of(user));
    view.init();
  }

  private void assertNoExecuteJs() {
    assertFalse(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains(EXIT_SWITCH_USER_FORM)
            || i.getInvocation().getExpression().contains(SIGNOUT_URL)));
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(TABS, view.tabs.getId().orElse(""));
    assertEquals(styleName(SUBMISSIONS, TAB), view.submissions.getId().orElse(""));
    assertEquals(styleName(PROFILE, TAB), view.profile.getId().orElse(""));
    assertEquals(styleName(USERS, TAB), view.users.getId().orElse(""));
    assertEquals(styleName(EXIT_SWITCH_USER, TAB), view.exitSwitchUser.getId().orElse(""));
    assertEquals(styleName(EXIT_SWITCH_USER_FORM, TAB), view.exitSwitchUserForm.getId().orElse(""));
    assertEquals(SWITCH_USER_EXIT_URL, view.exitSwitchUserForm.getElement().getAttribute("action"));
    assertEquals("post", view.exitSwitchUserForm.getElement().getAttribute("method"));
    assertEquals("none", view.exitSwitchUserForm.getElement().getStyle().get("display"));
    assertEquals(styleName(SIGNOUT, TAB), view.signout.getId().orElse(""));
    assertEquals(styleName(CHANGE_LANGUAGE, TAB), view.changeLanguage.getId().orElse(""));
    assertEquals(styleName(CONTACT, TAB), view.contact.getId().orElse(""));
    assertEquals(styleName(GUIDELINES, TAB), view.guidelines.getId().orElse(""));
    assertEquals(styleName(EDIT, TAB), view.edit.getId().orElse(""));
    assertEquals(styleName(PRINT, TAB), view.print.getId().orElse(""));
    assertEquals(styleName(HISTORY, TAB), view.history.getId().orElse(""));
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
    assertEquals(resources.message(HISTORY), view.history.getLabel());
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(ViewLayout.class, locale);
    ui.setLocale(locale);
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
    assertEquals(resources.message(HISTORY), view.history.getLabel());
  }

  @Test
  public void tabs() {
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertFalse(view.exitSwitchUserForm.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  public void tabs_AllowUsersView() {
    when(authenticatedUser.isAuthorized(UsersView.class)).thenReturn(true);
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertFalse(view.exitSwitchUserForm.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  public void tabs_SwitchedUser() {
    when(authenticatedUser.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)).thenReturn(true);
    view.init();
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertTrue(view.exitSwitchUser.isVisible());
    assertTrue(view.exitSwitchUserForm.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void tabs_SelectSubmissions() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.submissions);

    verify(navigationListener).afterNavigation(any());
    assertCurrentView(SubmissionsView.class);
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectSubmissionsNoChange() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.submissions);

    verify(navigationListener, never()).afterNavigation(any());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void tabs_SelectProfile() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.profile);

    verify(navigationListener).afterNavigation(any());
    assertCurrentView(ProfileView.class);
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectProfileNoChange() {
    Location location = new Location(ProfileView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.profile);

    verify(navigationListener, never()).afterNavigation(any());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void tabs_SelectUsers() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.users);

    verify(navigationListener).afterNavigation(any());
    assertCurrentView(UsersView.class);
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectUsersNoChange() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.users);

    verify(navigationListener, never()).afterNavigation(any());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void tabs_SelectContact() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.contact);

    verify(navigationListener).afterNavigation(any());
    assertCurrentView(ContactView.class);
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectContactNoChange() {
    Location location = new Location(ContactView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.contact);

    verify(navigationListener, never()).afterNavigation(any());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void tabs_SelectGuidelines() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.guidelines);

    verify(navigationListener).afterNavigation(any());
    assertCurrentView(GuidelinesView.class);
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectGuidelinesNoChange() {
    Location location = new Location(GuidelinesView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.guidelines);

    verify(navigationListener, never()).afterNavigation(any());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectExitSwitchUser() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.exitSwitchUser);

    verify(navigationListener, never()).afterNavigation(any());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().equals("document.getElementById(\""
            + styleName(EXIT_SWITCH_USER_FORM, TAB) + "\").submit()")));
  }

  @Test
  public void tabs_SelectSignout() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.signout);

    verify(navigationListener, never()).afterNavigation(any());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream().anyMatch(
        i -> i.getInvocation().getExpression().equals("location.assign('" + SIGNOUT_URL + "')")));
  }

  @Test
  public void tabs_ChangeLanguage_ToFrench() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(FRENCH, ui.getLocale());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToEnglish() {
    ui.setLocale(FRENCH);
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(ENGLISH, ui.getLocale());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToFrenchFromUsers() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    view.afterNavigation(afterNavigationEvent);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(FRENCH, ui.getLocale());
    assertEquals(view.users, view.tabs.getSelectedTab());
  }

  @Test
  public void afterNavigation_Submissions() {
    Location location = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.submissions, view.tabs.getSelectedTab());
    verify(navigationListener, never()).afterNavigation(any());
  }

  @Test
  public void afterNavigation_Users() {
    Location location = new Location(UsersView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.users, view.tabs.getSelectedTab());
    verify(navigationListener, never()).afterNavigation(any());
  }

  @Test
  public void afterNavigation_Contact() {
    Location location = new Location(ContactView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.contact, view.tabs.getSelectedTab());
    verify(navigationListener, never()).afterNavigation(any());
  }

  @Test
  public void afterNavigation_Guidelines() {
    Location location = new Location(GuidelinesView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.guidelines, view.tabs.getSelectedTab());
    verify(navigationListener, never()).afterNavigation(any());
  }

  @Test
  public void afterNavigation_Add() {
    Location location = new Location(SubmissionView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.add, view.tabs.getSelectedTab());
    assertTrue(view.add.isVisible());
    verify(navigationListener, never()).afterNavigation(any());
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
    verify(navigationListener, never()).afterNavigation(any());
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
    verify(navigationListener, never()).afterNavigation(any());
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

  @Test
  public void afterNavigation_History() {
    Location location = new Location(HistoryView.VIEW_NAME + "/12");
    when(afterNavigationEvent.getLocation()).thenReturn(location);

    view.afterNavigation(afterNavigationEvent);

    assertEquals(view.history, view.tabs.getSelectedTab());
    assertTrue(view.history.isVisible());
    verify(navigationListener, never()).afterNavigation(any());
  }

  @Test
  public void afterNavigation_HistoryHideAfterChange() {
    Location location1 = new Location(HistoryView.VIEW_NAME + "/12");
    Location location2 = new Location(SubmissionsView.VIEW_NAME);
    when(afterNavigationEvent.getLocation()).thenReturn(location1, location2);

    view.afterNavigation(afterNavigationEvent);
    view.afterNavigation(afterNavigationEvent);

    assertFalse(view.history.isVisible());
  }

  /**
   * Fake view.
   */
  public static class ViewTest {
  }
}
