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

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.Rules;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Objects;

public class VelocityEngineProviderTest {
  private VelocityEngineProvider velocityEngineProvider;
  @Rule
  public RuleChain rules = Rules.defaultRules(this);

  @Before
  public void beforeTest() {
    velocityEngineProvider = new VelocityEngineProvider();
  }

  private String getStringProperty(VelocityEngine velocityEngine, String key) {
    Object property = velocityEngine.getProperty(key);
    if (property instanceof Iterable && ((Iterable<?>) property).iterator().hasNext()) {
      return Objects.toString(((Iterable<?>) property).iterator().next());
    } else {
      return Objects.toString(property);
    }
  }

  @Test
  public void get() {
    VelocityEngine velocityEngine = velocityEngineProvider.get();

    assertEquals("class", getStringProperty(velocityEngine, RuntimeConstants.RESOURCE_LOADER));
    assertEquals(ClasspathResourceLoader.class.getName(),
        velocityEngine.getProperty("class.resource.loader.class"));
    assertEquals(Log4JLogChute.class.getName(),
        velocityEngine.getProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS));
    assertEquals("org.apache.velocity",
        velocityEngine.getProperty("runtime.log.logsystem.log4j.logger"));
  }
}
