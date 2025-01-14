package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.server.VaadinSession;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for {@link PathStreamResourceWriter}.
 */
@NonTransactionalTestAnnotations
public class PathStreamResourceWriterTest {
  private PathStreamResourceWriter writer;
  private Path path;
  @Mock
  private VaadinSession session;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() throws Throwable {
    path = Paths.get(Objects.requireNonNull(getClass().getResource("/structure1.png")).toURI());
    writer = new PathStreamResourceWriter(path.toFile());
  }

  @Test
  public void accept() throws Throwable {
    byte[] bytes = Files.readAllBytes(path);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    writer.accept(output, session);
    assertArrayEquals(bytes, output.toByteArray());
  }
}
