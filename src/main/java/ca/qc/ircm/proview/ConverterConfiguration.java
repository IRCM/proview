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
