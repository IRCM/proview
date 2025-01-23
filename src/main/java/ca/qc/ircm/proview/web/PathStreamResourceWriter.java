package ca.qc.ircm.proview.web;

import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.nio.file.Files;

/**
 * A {@link StreamResourceWriter} that sends a file.
 */
public class PathStreamResourceWriter implements StreamResourceWriter {
  @Serial
  private static final long serialVersionUID = 673747187193922551L;
  private File file;

  public PathStreamResourceWriter(File file) {
    this.file = file;
  }

  @Override
  public void accept(OutputStream stream, VaadinSession session) throws IOException {
    Files.copy(file.toPath(), stream);
  }
}
