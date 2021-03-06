/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.server.VaadinSession;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    path = Paths.get(getClass().getResource("/structure1.png").toURI());
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
