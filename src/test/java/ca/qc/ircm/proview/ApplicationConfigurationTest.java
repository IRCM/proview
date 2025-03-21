package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link ApplicationConfiguration}.
 */
@NonTransactionalTestAnnotations
public class ApplicationConfigurationTest {

  @Autowired
  private ApplicationConfiguration applicationConfiguration;

  @Test
  public void getLogFile() {
    assertEquals(Paths.get(System.getProperty("user.dir"), "test.log"),
        applicationConfiguration.getLogFile());
  }

  @Test
  public void getUrl() {
    assertEquals("http://localhost:8080/myurl/subpath?param1=abc",
        applicationConfiguration.getUrl("myurl/subpath?param1=abc"));
  }
}
