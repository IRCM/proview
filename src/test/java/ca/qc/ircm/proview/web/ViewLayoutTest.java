package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.security.web.WebSecurityConfiguration.SIGNOUT_URL;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.DRAWER_TOGGLE;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER_FORM;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.HEADER;
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

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final String MESSAGES_PREFIX = messagePrefix(ViewLayout.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private ViewLayout view;
  @MockBean
  private SwitchUserService switchUserService;
  @Mock
  private AfterNavigationListener navigationListener;
  @Autowired
  private SubmissionRepository submissionRepository;
  private Locale locale = ENGLISH;
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
    assertEquals(styleName(APPLICATION_NAME), view.applicationName.getId().orElse(""));
    assertEquals(styleName(ID, HEADER), view.header.getId().orElse(""));
    assertEquals(DRAWER_TOGGLE, view.drawerToggle.getId().orElse(""));
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
    assertEquals(styleName(HISTORY, TAB), view.history.getId().orElse(""));
    assertFalse(view.isDrawerOpened());
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME),
        view.applicationName.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS), view.submissions.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + PROFILE), view.profile.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS), view.users.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EXIT_SWITCH_USER),
        view.exitSwitchUser.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SIGNOUT), view.signout.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE),
        view.changeLanguage.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.contact.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.guidelines.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EDIT), view.edit.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HISTORY), view.history.getLabel());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME),
        view.applicationName.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS), view.submissions.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + PROFILE), view.profile.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS), view.users.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EXIT_SWITCH_USER),
        view.exitSwitchUser.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SIGNOUT), view.signout.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE),
        view.changeLanguage.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.contact.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.guidelines.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EDIT), view.edit.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HISTORY), view.history.getLabel());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS), view.header.getText());
    assertTrue($(SubmissionsView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectSubmissionsNoChange() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.submissions.getLabel());

    verify(navigationListener, never()).afterNavigation(any());
    assertEquals(view.submissions, view.tabs.getSelectedTab());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS), view.header.getText());
    assertTrue($(SubmissionsView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectProfile() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.profile.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.profile, view.tabs.getSelectedTab());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + PROFILE), view.header.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + PROFILE), view.header.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS), view.header.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS), view.header.getText());
    assertTrue($(UsersView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectContact() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.contact.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.contact, view.tabs.getSelectedTab());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.header.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.header.getText());
    assertTrue($(ContactView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectGuidelines() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.tabs).select(view.guidelines.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.guidelines, view.tabs.getSelectedTab());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.header.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.header.getText());
    assertTrue($(GuidelinesView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void tabs_SelectExitSwitchUser() {
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.exitSwitchUser);

    verify(switchUserService).exitSwitchUser(VaadinServletRequest.getCurrent());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains("window.open($0, $1)")
            && i.getInvocation().getParameters().size() > 0
            && i.getInvocation().getParameters().get(0).equals("/")));
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
    assertEquals(
        view.getTranslation(messagePrefix(SubmissionView.class) + SubmissionView.HEADER, 0, ""),
        view.header.getText());
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
    Submission submission = submissionRepository.findById(35L).get();
    assertEquals(view.getTranslation(messagePrefix(SubmissionView.class) + SubmissionView.HEADER, 1,
        submission.getExperiment()), view.header.getText());
  }

  @Test
  public void tabs_EditHideAfterChange() {
    navigate(SubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    view.tabs.setSelectedTab(view.submissions);

    assertFalse(view.edit.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void tabs_History() {
    navigate(HistoryView.class, 35L);
    view = $(ViewLayout.class).first();

    assertEquals(view.history, view.tabs.getSelectedTab());
    assertTrue(view.history.isVisible());
    Submission submission = submissionRepository.findById(35L).get();
    assertEquals(view.getTranslation(messagePrefix(HistoryView.class) + HistoryView.HEADER,
        submission.getExperiment()), view.header.getText());
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
