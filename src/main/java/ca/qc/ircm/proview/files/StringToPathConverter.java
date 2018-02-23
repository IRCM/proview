package ca.qc.ircm.proview.files;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Converts {@link String} into a {@link Path}.
 */
@Component
public class StringToPathConverter implements Converter<String, Path> {
  @Override
  public Path convert(String source) {
    return Paths.get(source);
  }
}
