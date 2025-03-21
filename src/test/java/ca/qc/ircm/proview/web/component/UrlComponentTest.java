package ca.qc.ircm.proview.web.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.web.UseForgotPasswordView;
import ca.qc.ircm.proview.web.ContactView;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UrlComponent}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class UrlComponentTest extends SpringUIUnitTest {

  private final UrlComponentForTest urlComponent = new UrlComponentForTest();

  @Test
  public void getUrl() {
    String url = urlComponent.getUrl(ContactView.class);
    assertEquals(ContactView.VIEW_NAME, url);
  }

  @Test
  public void getUrl_Parameters() {
    String url = urlComponent.getUrl(UseForgotPasswordView.class, "test/sub");
    assertEquals(UseForgotPasswordView.VIEW_NAME + "/test/sub", url);
  }

  @Test
  public void getUrlWithContextPath() {
    String url = urlComponent.getUrlWithContextPath(ContactView.class);
    assertEquals("/" + ContactView.VIEW_NAME, url);
  }

  @Test
  public void getUrlWithContextPath_Parameters() {
    String url = urlComponent.getUrlWithContextPath(UseForgotPasswordView.class, "test/sub");
    assertEquals("/" + UseForgotPasswordView.VIEW_NAME + "/test/sub", url);
  }

  private static class UrlComponentForTest implements UrlComponent {

  }
}
