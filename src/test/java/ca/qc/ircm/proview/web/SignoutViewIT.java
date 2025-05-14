package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SignoutView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SignoutViewIT extends AbstractTestBenchTestCase {

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    openView(SignoutView.VIEW_NAME);

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void security_User() {
    openView(SignoutView.VIEW_NAME);

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void signout() {
    openView(SubmissionsView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    openView(SignoutView.VIEW_NAME);
    $(SigninViewElement.class).waitForFirst();
  }
}
