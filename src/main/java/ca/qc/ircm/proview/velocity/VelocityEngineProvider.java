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

package ca.qc.ircm.proview.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;

/**
 * Provider for {@link VelocityEngine velocity engine}.
 */
@Configuration
public class VelocityEngineProvider implements Provider<VelocityEngine> {
  private static final String VELOCITY_RESOURCE_LOADER = "class";
  private static final String VELOCITY_RESOURCE_LOADER_CLASS =
      VELOCITY_RESOURCE_LOADER + "." + RuntimeConstants.RESOURCE_LOADER + ".class";
  private static final String RUNTIME_LOG_LOGSYSTEM_LOG4J_LOGGER =
      "runtime.log.logsystem.log4j.logger";
  private static final String VELOCITY_LOGGER = "org.apache.velocity";

  @Override
  @Bean
  public VelocityEngine get() {
    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(RuntimeConstants.RESOURCE_LOADER, VELOCITY_RESOURCE_LOADER);
    engine.setProperty(VELOCITY_RESOURCE_LOADER_CLASS, ClasspathResourceLoader.class.getName());
    engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
    engine.setProperty(RUNTIME_LOG_LOGSYSTEM_LOG4J_LOGGER, VELOCITY_LOGGER);
    engine.init();
    return engine;
  }
}
