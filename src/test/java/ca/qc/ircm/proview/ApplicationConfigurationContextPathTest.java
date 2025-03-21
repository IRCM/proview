package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests for {@link ApplicationConfiguration}.
 */
@NonTransactionalTestAnnotations
@ActiveProfiles({"test", "context-path"})
public class ApplicationConfigurationContextPathTest {

  @Autowired
  private ApplicationConfiguration applicationConfiguration;

  @Test
  public void getUrl() {
    assertEquals("http://localhost:8080/proview-test/myurl/subpath?param1=abc",
        applicationConfiguration.getUrl("myurl/subpath?param1=abc"));
  }
}
