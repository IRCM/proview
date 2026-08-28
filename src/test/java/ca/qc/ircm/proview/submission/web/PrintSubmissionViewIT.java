package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.VIEW_NAME;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link PrintSubmissionView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PrintSubmissionViewIT extends SpringBrowserlessTest {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(PrintSubmissionViewIT.class);

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME + "/164", SigninView.class);
  }

  @Test
  public void submissionsView() {
    PrintSubmissionView view = navigate(PrintSubmissionView.class, 164L);

    test(view.submissionsView).click();

    find(SubmissionsView.class).single();
  }
}
