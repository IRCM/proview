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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link ViewLayout}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ViewLayoutTest extends SpringUIUnitTest {
  private ViewLayout view;
  @MockBean
  private SwitchUserService switchUserService;
  @Mock
  private AfterNavigationListener navigationListener;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ViewLayout.class, locale);
  private User user = new User(1L, "myuser");

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    navigate(SubmissionsView.class);
    view = $(ViewLayout.class).first();
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
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(ViewLayout.class, locale);
    UI.getCurrent().setLocale(locale);
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
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
    assertFalse(view.edit.isVisible());
    assertFalse(view.print.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  public void tabs_SelectSubmissions() {
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.submissions.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
    assertTrue($(SubmissionsView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectSubmissionsNoChange() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.submissions.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
    assertTrue($(SubmissionsView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectProfile() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.profile.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.profile, view.tabs.getSelectedTab());
    assertTrue($(ProfileView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectProfileNoChange() {
    navigate(ProfileView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.profile.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.profile, view.tabs.getSelectedTab());
    assertTrue($(ProfileView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_SelectUsers() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.users.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.users, view.tabs.getSelectedTab());
    assertTrue($(UsersView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_SelectUsersNoChange() {
    navigate(UsersView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.users.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.users, view.tabs.getSelectedTab());
    assertTrue($(UsersView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectContact() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.contact.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.contact, view.tabs.getSelectedTab());
    assertTrue($(ContactView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectContactNoChange() {
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.contact.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.contact, view.tabs.getSelectedTab());
    assertTrue($(ContactView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectGuidelines() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.guidelines.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.guidelines, view.tabs.getSelectedTab());
    assertTrue($(GuidelinesView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectGuidelinesNoChange() {
    navigate(GuidelinesView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.guidelines.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.guidelines, view.tabs.getSelectedTab());
    assertTrue($(GuidelinesView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectExitSwitchUser() {
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    view.tabs.setSelectedTab(view.exitSwitchUser);

    verify(switchUserService).exitSwitchUser();
    verify(navigationListener).afterNavigation(any());
    assertTrue($(SubmissionsView.class).exists());
  }

  @Test
  @Disabled("Fails because of invalidated session")
  public void tabs_SelectSignout() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.signout.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void tabs_ChangeLanguage_ToFrench() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.changeLanguage.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(FRENCH, UI.getCurrent().getLocale());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_ChangeLanguage_ToEnglish() {
    UI.getCurrent().setLocale(Locale.FRENCH);
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.changeLanguage.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(ENGLISH, UI.getCurrent().getLocale());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_ChangeLanguage_ToFrenchFromUsers() {
    navigate(UsersView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    view.tabs.setSelectedTab(view.changeLanguage);

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(FRENCH, UI.getCurrent().getLocale());
    assertEquals(view.users, view.tabs.getSelectedTab());
  }

  @Test
  public void tabs_Add() {
    navigate(SubmissionView.class);
    view = $(ViewLayout.class).first();

    assertEquals(view.add, view.tabs.getSelectedTab());
    assertTrue(view.add.isVisible());
  }

  @Test
  public void tabs_AddHideAfterChange() {
    navigate(SubmissionView.class);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.submissions);

    assertFalse(view.add.isVisible());
  }

  @Test
  public void tabs_Edit() {
    navigate(SubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    assertEquals(view.edit, view.tabs.getSelectedTab());
    assertTrue(view.edit.isVisible());
  }

  @Test
  public void tabs_EditHideAfterChange() {
    navigate(SubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.submissions);

    assertFalse(view.edit.isVisible());
  }

  @Test
  public void afterNavigation_Print() {
    navigate(PrintSubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    assertEquals(view.print, view.tabs.getSelectedTab());
    assertTrue(view.print.isVisible());
  }

  @Test
  public void tabs_PrintHideAfterChange() {
    navigate(PrintSubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.submissions);

    assertFalse(view.print.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_History() {
    navigate(HistoryView.class, 35L);
    view = $(ViewLayout.class).first();

    assertEquals(view.history, view.tabs.getSelectedTab());
    assertTrue(view.history.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_HistoryHideAfterChange() {
    navigate(HistoryView.class, 35L);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.submissions);

    assertFalse(view.history.isVisible());
  }

  @Test
  public void tabs_UserVisibility() {
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void tabs_ManagerVisibility() {
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_AdminVisibility() {
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithMockUser(
      username = "christopher.anderson@ircm.qc.ca",
      roles = { "USER", "PREVIOUS_ADMINISTRATOR" })
  public void tabs_SwitchedUserVisibility() {
    assertFalse(view.users.isVisible());
    assertTrue(view.exitSwitchUser.isVisible());
  }
}
