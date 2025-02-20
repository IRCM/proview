package ca.qc.ircm.proview.web.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
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
  public void getUrl_Class() {
    String url = urlComponent.getUrl(ContactView.class);
    assertEquals("/" + ContactView.VIEW_NAME, url);
  }

  @Test
  public void getUrl_String() {
    String url = urlComponent.getUrl(ContactView.VIEW_NAME);
    assertEquals("/" + ContactView.VIEW_NAME, url);
  }

  private static class UrlComponentForTest implements UrlComponent {

  }
}
