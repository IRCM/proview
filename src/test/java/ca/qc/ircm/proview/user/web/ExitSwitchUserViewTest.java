package ca.qc.ircm.proview.user.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link ExitSwitchUserView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ExitSwitchUserViewTest extends SpringBrowserlessTest {

  @Test
  @WithMockUser(username = "christopher.anderson@ircm.qc.ca", roles = {"USER",
      "PREVIOUS_ADMINISTRATOR"})
  public void exitSwitchUser() {
    navigate(ExitSwitchUserView.class);

    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream().anyMatch(
        i -> i.getInvocation().getExpression().contains("window.open($0, $1)") && !i.getInvocation()
            .getParameters().isEmpty() && i.getInvocation().getParameters().getFirst()
            .equals("/impersonate/exit")));
  }
}
