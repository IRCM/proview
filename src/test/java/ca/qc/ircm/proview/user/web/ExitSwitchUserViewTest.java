package ca.qc.ircm.proview.user.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link ExitSwitchUserView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ExitSwitchUserViewTest extends SpringUIUnitTest {

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
