package ca.qc.ircm.proview;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts {@link String} into a {@link Duration}.
 */
@Component
@ConfigurationPropertiesBinding
public class DurationConverter implements Converter<String, Duration> {
  @Override
  public Duration convert(String source) {
    return Duration.parse(source);
  }
}
