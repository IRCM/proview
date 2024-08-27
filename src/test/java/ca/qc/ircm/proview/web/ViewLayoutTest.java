package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
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
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.ViewLayout.NAV;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIDE_NAV;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
  @SpyBean
  private SwitchUserService switchUserService;
  @Mock
  private AfterNavigationListener navigationListener;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Autowired
  private UserRepository userRepository;
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
    assertEquals(SIDE_NAV, view.sideNav.getId().orElse(""));
    assertEquals(styleName(SUBMISSIONS, NAV), view.submissions.getId().orElse(""));
    assertEquals(styleName(PROFILE, NAV), view.profile.getId().orElse(""));
    assertEquals(styleName(USERS, NAV), view.users.getId().orElse(""));
    assertEquals(styleName(EXIT_SWITCH_USER, NAV), view.exitSwitchUser.getId().orElse(""));
    assertEquals(styleName(SIGNOUT, NAV), view.signout.getId().orElse(""));
    assertEquals(styleName(CONTACT, NAV), view.contact.getId().orElse(""));
    assertEquals(styleName(GUIDELINES, NAV), view.guidelines.getId().orElse(""));
    assertEquals(CHANGE_LANGUAGE, view.changeLanguage.getId().orElse(""));
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.contact.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.guidelines.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE),
        view.changeLanguage.getText());
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.contact.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.guidelines.getLabel());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE),
        view.changeLanguage.getText());
  }

  @Test
  public void sideNavItems() {
    assertTrue(view.submissions.isVisible());
    assertTrue(view.profile.isVisible());
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
    assertTrue(view.signout.isVisible());
    assertTrue(view.contact.isVisible());
    assertTrue(view.guidelines.isVisible());
  }

  @Test
  public void sideNav_SelectSubmissions() {
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.sideNav).clickItem(view.submissions.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.submissions, view.selectedSideNavItem().orElse(null));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS), view.header.getText());
    assertTrue($(SubmissionsView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void sideNav_SelectProfile() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.sideNav).clickItem(view.profile.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.profile, view.selectedSideNavItem().orElse(null));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + PROFILE), view.header.getText());
    assertTrue($(ProfileView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void sideNav_SelectUsers() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.sideNav).clickItem(view.users.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.users, view.selectedSideNavItem().orElse(null));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS), view.header.getText());
    assertTrue($(UsersView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void sideNav_SelectContact() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.sideNav).clickItem(view.contact.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.contact, view.selectedSideNavItem().orElse(null));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + CONTACT), view.header.getText());
    assertTrue($(ContactView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  public void sideNav_SelectGuidelines() {
    UI.getCurrent().addAfterNavigationListener(navigationListener);

    test(view.sideNav).clickItem(view.guidelines.getLabel());

    verify(navigationListener).afterNavigation(any());
    assertEquals(view.guidelines, view.selectedSideNavItem().orElse(null));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + GUIDELINES), view.header.getText());
    assertTrue($(GuidelinesView.class).exists());
    assertNoExecuteJs();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void sideNav_SelectExitSwitchUser() {
    switchUserService.switchUser(userRepository.findById(10L).get(),
        VaadinServletRequest.getCurrent());
    navigate(ContactView.class);
    view = $(ViewLayout.class).first();

    test(view.sideNav).clickItem(view.exitSwitchUser.getLabel());

    verify(switchUserService).exitSwitchUser(VaadinServletRequest.getCurrent());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains("window.open($0, $1)")
            && i.getInvocation().getParameters().size() > 0
            && i.getInvocation().getParameters().get(0).equals("/")));
  }

  @Test
  public void sideNav_SelectSignout() {
    // Invalidated session.
    test(view.sideNav).clickItem(view.signout.getLabel());
    assertThrows(IllegalStateException.class, () -> {
      VaadinServletRequest.getCurrent().getWrappedSession(false).getAttributeNames();
    });

    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains("window.open($0, $1)")
            && i.getInvocation().getParameters().size() > 0
            && i.getInvocation().getParameters().get(0).equals("/")));
  }

  @Test
  public void sideNav_ChangeLanguage_ToFrench() {
    test(view.changeLanguage).click();

    assertEquals(FRENCH, UI.getCurrent().getLocale());
    assertEquals(view.submissions, view.selectedSideNavItem().orElse(null));
  }

  @Test
  public void sideNav_ChangeLanguage_ToEnglish() {
    UI.getCurrent().setLocale(Locale.FRENCH);

    test(view.changeLanguage).click();

    assertEquals(ENGLISH, UI.getCurrent().getLocale());
    assertEquals(view.submissions, view.selectedSideNavItem().orElse(null));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void sideNav_ChangeLanguage_ToFrenchFromUsers() {
    navigate(UsersView.class);
    view = $(ViewLayout.class).first();

    test(view.changeLanguage).click();

    assertEquals(FRENCH, UI.getCurrent().getLocale());
    assertEquals(view.users, view.selectedSideNavItem().orElse(null));
  }

  @Test
  public void sideNav_AddSubmission() {
    navigate(SubmissionView.class);
    view = $(ViewLayout.class).first();

    assertFalse(view.selectedSideNavItem().isPresent());
    assertEquals(
        view.getTranslation(messagePrefix(SubmissionView.class) + SubmissionView.HEADER, 0, ""),
        view.header.getText());
  }

  @Test
  public void sideNav_EditSubmission() {
    navigate(SubmissionView.class, 35L);
    view = $(ViewLayout.class).first();

    assertFalse(view.selectedSideNavItem().isPresent());
    Submission submission = submissionRepository.findById(35L).get();
    assertEquals(view.getTranslation(messagePrefix(SubmissionView.class) + SubmissionView.HEADER, 1,
        submission.getExperiment()), view.header.getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void sideNav_History() {
    navigate(HistoryView.class, 35L);
    view = $(ViewLayout.class).first();

    assertFalse(view.selectedSideNavItem().isPresent());
    Submission submission = submissionRepository.findById(35L).get();
    assertEquals(view.getTranslation(messagePrefix(HistoryView.class) + HistoryView.HEADER,
        submission.getExperiment()), view.header.getText());
  }

  @Test
  public void sideNav_UserVisibility() {
    assertFalse(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void sideNav_ManagerVisibility() {
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void sideNav_AdminVisibility() {
    assertTrue(view.users.isVisible());
    assertFalse(view.exitSwitchUser.isVisible());
  }

  @Test
  @WithMockUser(
      username = "christopher.anderson@ircm.qc.ca",
      roles = { "USER", "PREVIOUS_ADMINISTRATOR" })
  public void sideNav_SwitchedUserVisibility() {
    assertFalse(view.users.isVisible());
    assertTrue(view.exitSwitchUser.isVisible());
  }
}
