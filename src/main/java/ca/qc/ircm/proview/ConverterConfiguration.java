package ca.qc.ircm.proview;

import ca.qc.ircm.proview.files.StringToPathConverter;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

/**
 * Configuration for Spring.
 */
@Configuration
public class ConverterConfiguration {
  private Set<Converter<?, ?>> getConverters() {
    Set<Converter<?, ?>> converters = new HashSet<>();
    converters.add(new StringToPathConverter());
    converters.add(new DurationConverter());

    return converters;
  }

  /**
   * Returns conversion service with custom converters.
   *
   * @return conversion service with custom converters
   */
  @Bean(name = "conversionService")
  public ConversionService getConversionService() {
    ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
    bean.setConverters(getConverters());
    bean.afterPropertiesSet();
    return bean.getObject();
  }
}
