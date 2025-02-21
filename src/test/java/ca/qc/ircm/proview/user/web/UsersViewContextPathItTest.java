package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@ActiveProfiles({"integration-test", "context-path"})
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewContextPathItTest extends AbstractTestBenchTestCase {

  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void switchUser() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(5);

    view.switchUser().click();

    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewLayout = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(viewLayout::exitSwitchUser).isPresent());
    assertFalse(optional(viewLayout::users).isPresent());
  }
}
