package ca.qc.ircm.proview.files.web;

import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link StreamResourceWriter} that sends a file.
 */
public class PathStreamResourceWriter implements StreamResourceWriter {
  private static final long serialVersionUID = 673747187193922551L;
  private Path path;

  public PathStreamResourceWriter(Path path) {
    this.path = path;
  }

  @Override
  public void accept(OutputStream stream, VaadinSession session) throws IOException {
    Files.copy(path, stream);
  }
}
