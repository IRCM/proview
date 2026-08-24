package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ViewLayout}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ViewLayoutIT extends SpringUIUnitTest {

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  public void submissions() {
    navigate(ContactView.class);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.sideNav).clickItem(view.submissions.getLabel());
    $(SubmissionsView.class).single();
  }

  @Test
  public void profile() {
    navigate(ContactView.class);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.sideNav).clickItem(view.profile.getLabel());
    $(ProfileView.class).single();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void users() {
    navigate(ContactView.class);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.sideNav).clickItem(view.users.getLabel());
    $(UsersView.class).single();
  }

  @Test
  public void changeLanguage() {
    navigate(ContactView.class);
    assertEquals(UI.getCurrent().getLocale(), ENGLISH);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.changeLanguage).click();
    $(ContactView.class).single();
    assertEquals(UI.getCurrent().getLocale(), FRENCH);
  }

  @Test
  public void contact() {
    navigate(GuidelinesView.class);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.sideNav).clickItem(view.contact.getLabel());
    $(ContactView.class).single();
  }

  @Test
  public void guidelines() {
    navigate(ContactView.class);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.sideNav).clickItem(view.guidelines.getLabel());
    $(GuidelinesView.class).single();
  }
}
